package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.customExceptions.walletExceptions.InsufficientFundsException;
import ru.kalimulin.customExceptions.walletExceptions.InvalidPinException;
import ru.kalimulin.customExceptions.walletExceptions.PaymentProcessingException;
import ru.kalimulin.customExceptions.walletExceptions.WalletNotFoundException;
import ru.kalimulin.dto.kafkaEventDTO.WalletNotificationEvent;
import ru.kalimulin.dto.kafkaEventDTO.WalletTransactionEvent;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdateBalanceDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdatePinDTO;
import ru.kalimulin.kafka.KafkaEmailEventPublisher;
import ru.kalimulin.kafka.WalletEventProducer;
import ru.kalimulin.mappers.walletMapper.WalletMapper;
import ru.kalimulin.models.User;
import ru.kalimulin.models.Wallet;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.repositories.WalletRepository;
import ru.kalimulin.service.WalletService;
import ru.kalimulin.stubService.PaymentService;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final PaymentService paymentService;
    private final WalletEventProducer walletEventProducer;
    private final KafkaEmailEventPublisher kafkaEmailEventPublisher;

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Transactional
    @Override
    public WalletResponseDTO createWallet(WalletCreateDTO walletCreateDTO, HttpSession session) {
        logger.info("Создание нового кошелька для пользователя");

        String walletNumber;
        do {
            walletNumber = generateWalletNumber();
        } while (walletRepository.existsByWalletNumber(walletNumber));

        User user = findUserByLogin(SessionUtils.getUserLogin(session));

        Wallet wallet = Wallet.builder()
                .walletNumber(walletNumber)
                .user(user)
                .balance(BigDecimal.ZERO)
                .pin(hashPin(walletCreateDTO.getPin()))
                .build();

        walletRepository.save(wallet);

        logger.info("Кошелек создан");
        return walletMapper.toWalletResponseDTO(wallet);
    }

    @Transactional
    @Override
    public WalletResponseDTO getUserWallet(HttpSession session) {
        Wallet wallet = findWalletByUser(findUserByLogin(SessionUtils.getUserLogin(session)));
        logger.info("Запрос кошелька");
        return walletMapper.toWalletResponseDTO(wallet);
    }

    @Transactional
    @Override
    public void transfer(String walletNumber, BigDecimal amount, String pin, HttpSession session) {
        String senderLogin = SessionUtils.getUserLogin(session);

        User sender = userRepository.findByLogin(senderLogin)
                .orElseThrow(() -> {
                    logger.error("Не найден пользователь");
                    return new UserNotFoundException("Пользователь с логином " + senderLogin + "не найден");
                });

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> {
                    logger.error("Кошелек пользователя не найден");
                    return new WalletNotFoundException("Кошелек пользователя не найден");
                });


        Wallet recipientWallet = walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> {
                    logger.error("Кошелек с номером не найден");
                    return new WalletNotFoundException("Кошелька с номером " + walletNumber + " не существует");
                });

        validPin(senderWallet, pin); //Проверка пин

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            logger.error("Недостаточно средств у пользователя");
            throw new InsufficientFundsException("Недостаточно средств на кошельке");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        logger.info("Перевод");

        WalletNotificationEvent walletNotificationEventToSender = new WalletNotificationEvent(
                sender.getEmail(), "Успешный исходящий перевод",
                "Вы успешно перевели средства на " + walletNumber + " Время операции " + Instant.now()
        );
        kafkaEmailEventPublisher.sendWalletNotification(walletNotificationEventToSender);

        WalletNotificationEvent walletNotificationEvent = new WalletNotificationEvent(
                recipientWallet.getUser().getEmail(), "Успешный входящий перевод",
                "Ваш баланс пополнен на сумму: " + amount + ". Текущий баланс: " + recipientWallet.getBalance()
        );
        kafkaEmailEventPublisher.sendWalletNotification(walletNotificationEvent);

        WalletTransactionEvent event = new WalletTransactionEvent(
                "TRANSFER",
                senderLogin,
                walletNumber,
                amount,
                Instant.now()
        );
        walletEventProducer.sendWalletTransaction(event);
    }


    @Transactional
    @Override
    public void deposit(HttpSession session, WalletUpdateBalanceDTO walletUpdateBalanceDTO) {
        logger.info("Пополнение кошелька пользователя");
        Wallet wallet = findWalletByUser(findUserByLogin(SessionUtils.getUserLogin(session)));
        BigDecimal amount = walletUpdateBalanceDTO.getAmount();
        boolean payment = paymentService.processPayment(SessionUtils.getUserLogin(session), amount);

        if (!payment) {
            logger.error("Ошибка обработки платежа для пользователя");
            throw new PaymentProcessingException("Ошибка при обработке платежа. Повторите попытку позже");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        logger.info("Баланс пользователя успешно пополнен");

        WalletNotificationEvent walletNotificationEvent = new WalletNotificationEvent(
                wallet.getUser().getEmail(), "Пополнение кошелька",
                "Ваш кошелек успешно пополнен на " + amount + " RUB. Новый баланс: " + wallet.getBalance()
        );
        kafkaEmailEventPublisher.sendWalletNotification(walletNotificationEvent);

        WalletTransactionEvent walletTransactionEvent = new WalletTransactionEvent(
                "DEPOSIT",
                wallet.getUser().getLogin(),
                wallet.getWalletNumber(),
                amount,
                Instant.now()
        );
        walletEventProducer.sendWalletTransaction(walletTransactionEvent);


    }

    @Transactional
    @Override
    public void changePin(HttpSession session, WalletUpdatePinDTO walletUpdatePinDTO) {
        logger.info("Запрос на смену PIN-кода");

        User user = findUserByLogin(SessionUtils.getUserLogin(session));
        Wallet wallet = findWalletByUser(user);

        // Проверяем старый PIN перед изменением
        if (!BCrypt.checkpw(walletUpdatePinDTO.getOldPin(), wallet.getPin())) {
            logger.warn("Попытка смены PIN-кода с неверным старым PIN");
            throw new InvalidPinException("Неверный старый PIN-код");
        }

        wallet.setPin(hashPin(walletUpdatePinDTO.getNewPin()));
        walletRepository.save(wallet);

        logger.info("Пользователь успешно сменил PIN-код");

        WalletNotificationEvent walletNotificationEvent = new WalletNotificationEvent(
                wallet.getUser().getEmail(), "Изменение PIN-кода",
                "Ваш PIN-код успешно изменен! Время операции " + Instant.now()
        );
        kafkaEmailEventPublisher.sendWalletNotification(walletNotificationEvent);

        WalletTransactionEvent walletTransactionEvent = new WalletTransactionEvent(
                "PIN_CHANGE",
                user.getLogin(),
                wallet.getWalletNumber(),
                null,
                Instant.now()
        );
        walletEventProducer.sendWalletTransaction(walletTransactionEvent);
    }

    /**
     * Генерация номера кошелька
     *
     * @return номер из 12 цифр
     */
    private static String generateWalletNumber() {
        SecureRandom random = new SecureRandom();
        long number = Math.abs(random.nextLong()) % 1000000000000L; // Ограничиваем до 12 цифр
        return String.format("%012d", number);
    }

    /**
     * Получение пользователя по логину.
     *
     * @param login логин пользователя.
     * @return найденный пользователь.
     * @throws UserNotFoundException если пользователь с таким логином не найден.
     */
    private User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден");
                    return new UserNotFoundException("Пользователь с таким логином " + login + " не найден");
                });
    }

    /**
     * Получение кошелька пользователя
     *
     * @param user пользователь
     * @return кошелек
     */
    private Wallet findWalletByUser(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> {
                    logger.warn("Кошелек пользователя не найден");
                    return new WalletNotFoundException("Кошелек пользователя не найден");
                });
    }

    /**
     * Хэширует указанный PIN-код с использованием BCrypt.
     *
     * @param pin PIN-код, который необходимо хэшировать
     * @return Хэшированный PIN-код
     */
    private String hashPin(String pin) {
        return BCrypt.hashpw(pin, BCrypt.gensalt());
    }

    /**
     * Проверяет, соответствует ли указанный PIN
     * хэшированному PIN-коду
     *
     * @param wallet Кошелек у которого проверяем PIN
     * @param pin    PIN-код, который нужно проверить на соответствие
     * @return {@code true}, если указанный PIN-код соответствует хэшированному PIN-коду;
     * {@code false} в противном случае
     */
    private boolean checkPin(Wallet wallet, String pin) {
        return BCrypt.checkpw(pin, wallet.getPin());
    }

    /**
     * Проверяет, соответствует ли указанный PIN-код хэшированному PIN-коду на счету.
     *
     * @param wallet кошелек, для которой необходимо проверить PIN-код
     * @param pin    PIN-код, который нужно проверить на соответствие
     * @throws RuntimeException если указанный PIN-код не совпадает с хэшированным PIN-кодом кошелька
     */
    private void validPin(Wallet wallet, String pin) {
        if (!checkPin(wallet, pin)) {
            throw new InvalidPinException("Пин-код не прошел проверку");
        }
    }
}