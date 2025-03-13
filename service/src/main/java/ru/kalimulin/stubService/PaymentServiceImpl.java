package ru.kalimulin.stubService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.customExceptions.walletExceptions.InsufficientFundsException;
import ru.kalimulin.customExceptions.walletExceptions.WalletNotFoundException;
import ru.kalimulin.models.User;
import ru.kalimulin.models.Wallet;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.repositories.WalletRepository;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final Random random = new Random();
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Autowired
    public PaymentServiceImpl(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean processPayment(String userLogin, BigDecimal amount) {
        logger.info("Обработка платежа для пользователя {} на сумму {}", userLogin, amount);

        boolean payment = random.nextBoolean();

        if (payment) {
            logger.info("Платеж для {} на сумму {} успешно обработан", userLogin, amount);
            return true;
        } else {
            logger.error("Ошибка обработки платежа для пользователя {}", userLogin);
            return false;
        }
    }

    @Override
    public boolean processPayment(User user, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек пользователя не найден"));

        if(wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на кошельке");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        return true;
    }

    @Transactional
    public void withdrawFunds(String userLogin, BigDecimal amount) {
        logger.info("Попытка списания {} с кошелька пользователя {}", amount, userLogin);

        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с логином " + userLogin + " не найден"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Кошелек пользователя не найден"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            logger.error("Ошибка: Недостаточно средств у пользователя {}. Баланс: {}, требуемая сумма: {}",
                    userLogin, wallet.getBalance(), amount);
            throw new InsufficientFundsException("Недостаточно средств на балансе");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        logger.info("Успешное списание {} с кошелька пользователя {}. Новый баланс: {}",
                amount, userLogin, wallet.getBalance());
    }
}