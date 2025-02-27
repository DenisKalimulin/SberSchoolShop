package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.customExceptions.walletExceptions.InsufficientFundsException;
import ru.kalimulin.customExceptions.walletExceptions.InvalidPinException;
import ru.kalimulin.customExceptions.walletExceptions.PaymentProcessingException;
import ru.kalimulin.customExceptions.walletExceptions.WalletNotFoundException;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdateBalanceDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdatePinDTO;
import ru.kalimulin.mappers.walletMapper.WalletMapper;
import ru.kalimulin.models.User;
import ru.kalimulin.models.Wallet;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.repositories.WalletRepository;
import ru.kalimulin.service.WalletService;
import ru.kalimulin.stubService.PaymentService;
import ru.kalimulin.stubService.PaymentServiceImpl;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;
    private final PaymentService paymentService;

    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);


    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository, UserRepository userRepository,
                             WalletMapper walletMapper, PaymentServiceImpl paymentService) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.walletMapper = walletMapper;
        this.paymentService = paymentService;
    }

    @Transactional
    @Override
    public WalletResponseDTO createWallet(WalletCreateDTO walletCreateDTO, HttpSession session) {
        logger.info("Создание нового кошелька для пользователя: {}", SessionUtils.getUserLogin(session));

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

        logger.info("Кошелек создан для пользователя {} с номером {}", user.getLogin(), walletNumber);
        return walletMapper.toWalletResponseDTO(wallet);
    }

    @Transactional
    @Override
    public WalletResponseDTO getUserWallet(HttpSession session) {
        Wallet wallet = findWalletByUser(findUserByLogin(SessionUtils.getUserLogin(session)));
        logger.info("Запрос кошелька пользователя: {}", wallet.getUser().getLogin());
        return walletMapper.toWalletResponseDTO(wallet);
    }

    @Transactional
    @Override
    public void transfer(User buyer, User seller, BigDecimal amount) {
        logger.info("Перевод {} от {} к {}", amount, buyer.getLogin(), seller.getLogin());

        Wallet buyerWallet = findWalletByUser(buyer);

        Wallet sellerWallet = findWalletByUser(seller);

        if (buyerWallet.getBalance().compareTo(amount) < 0) {
            logger.warn("Ошибка перевода: недостаточно средств у {}", buyer.getLogin());
            throw new InsufficientFundsException("Недостаточно средств на балансе");
        }

        buyerWallet.setBalance(buyerWallet.getBalance().subtract(amount));
        sellerWallet.setBalance(sellerWallet.getBalance().add(amount));

        walletRepository.save(buyerWallet);
        walletRepository.save(sellerWallet);

        logger.info("Перевод завершен: {} -> {} на сумму {}", buyer.getLogin(), seller.getLogin(), amount);
    }

    @Transactional
    @Override
    public void transfer(String walletNumber, BigDecimal amount, String pin, HttpSession session) {
        String senderLogin = SessionUtils.getUserLogin(session);

        User sender = userRepository.findByLogin(senderLogin)
                .orElseThrow(() -> {
                    logger.error("Не найден пользователь с логином {}", SessionUtils.getUserLogin(session));
                    return new UserNotFoundException("Пользователь с логином " + senderLogin + "не найден");
                });

        Wallet senderWallet = walletRepository.findByUser(sender)
                .orElseThrow(() -> {
                    logger.error("Кошелек пользователя {} не найден", senderLogin);
                    return new WalletNotFoundException("Кошелек пользователя не найден");
                });


        Wallet recipientWallet = walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> {
                    logger.error("Кошелек с номером {} не найден", walletNumber);
                    return new WalletNotFoundException("Кошелька с номером " + walletNumber + " не существует");
                });

        validPin(senderWallet, pin); //Проверка пин

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            logger.error("Недостаточно средств у пользователя {}", senderLogin);
            throw new InsufficientFundsException("Недостаточно средств на кошельке");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        recipientWallet.setBalance(recipientWallet.getBalance().add(amount));

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        logger.info("Перевод {} от {} к {}", amount, senderWallet.getWalletNumber(), recipientWallet.getWalletNumber());
    }


    @Transactional
    @Override
    public void deposit(HttpSession session, WalletUpdateBalanceDTO walletUpdateBalanceDTO) {
        logger.info("Пополнение кошелька пользователя {} на сумму {}",
                SessionUtils.getUserLogin(session), walletUpdateBalanceDTO.getAmount());
        Wallet wallet = findWalletByUser(findUserByLogin(SessionUtils.getUserLogin(session)));
        BigDecimal amount = walletUpdateBalanceDTO.getAmount();
        boolean payment = paymentService.processPayment(SessionUtils.getUserLogin(session), amount);

        if (!payment) {
            logger.error("Ошибка обработки платежа для пользователя {}", SessionUtils.getUserLogin(session));
            throw new PaymentProcessingException("Ошибка при обработке платежа. Повторите попытку позже");
        }

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        logger.info("Баланс пользователя {} успешно пополнен на {}", SessionUtils.getUserLogin(session), amount);
    }

    @Transactional
    @Override
    public void changePin(HttpSession session, WalletUpdatePinDTO walletUpdatePinDTO) {
        logger.info("Запрос на смену PIN-кода для пользователя {}", SessionUtils.getUserLogin(session));

        User user = findUserByLogin(SessionUtils.getUserLogin(session));
        Wallet wallet = findWalletByUser(user);

        // Проверяем старый PIN перед изменением
        if (!BCrypt.checkpw(walletUpdatePinDTO.getOldPin(), wallet.getPin())) {
            logger.warn("Попытка смены PIN-кода с неверным старым PIN пользователем {}", SessionUtils.getUserLogin(session));
            throw new InvalidPinException("Неверный старый PIN-код");
        }

        wallet.setPin(hashPin(walletUpdatePinDTO.getNewPin()));
        walletRepository.save(wallet);

        logger.info("Пользователь {} успешно сменил PIN-код", SessionUtils.getUserLogin(session));

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
                    logger.warn("Пользователь с логином {} не найден", login);
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
                    logger.warn("Кошелек пользователя {} не найден", user.getLogin());
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
     * хэшированному PIN-коду из <L
     *
     * @param wallet Кошелек у которого проверяем PII
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