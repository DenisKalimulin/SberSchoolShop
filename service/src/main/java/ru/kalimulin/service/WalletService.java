package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.customExceptions.walletExceptions.InsufficientFundsException;
import ru.kalimulin.customExceptions.walletExceptions.InvalidPinException;
import ru.kalimulin.customExceptions.walletExceptions.PaymentProcessingException;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdateBalanceDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdatePinDTO;
import ru.kalimulin.models.User;

import java.math.BigDecimal;

/**
 * Интерфейс сервиса для работы с кошельками пользователей.
 */
public interface WalletService {

    /**
     * Создает новый кошелек для пользователя.
     *
     * @param walletCreateDTO данные для создания кошелька.
     * @param session         текущая сессия пользователя.
     * @return DTO с данными о созданном кошельке.
     */
    WalletResponseDTO createWallet(WalletCreateDTO walletCreateDTO, HttpSession session);

    /**
     * Получает информацию о кошельке текущего пользователя.
     *
     * @param session текущая сессия пользователя.
     * @return DTO с данными о кошельке.
     */
    WalletResponseDTO getUserWallet(HttpSession session);

    /**
     * Выполняет перевод средств от покупателя к продавцу.
     *
     * @param buyer  пользователь, который отправляет деньги.
     * @param seller пользователь, который получает деньги.
     * @param amount сумма перевода.
     * @throws InsufficientFundsException если у отправителя недостаточно средств.
     */
    void transfer(User buyer, User seller, BigDecimal amount);

    /**
     * Выполняет перевод денег с одного кошелька на другой.
     *
     * @param walletNumber номер кошелька получателя.
     * @param amount       сумма перевода.
     * @param pin          PIN-код отправителя.
     * @param session      текущая сессия отправителя.
     * @throws InsufficientFundsException если у отправителя недостаточно средств.
     * @throws InvalidPinException        если PIN-код неверный.
     */
    void transfer(String walletNumber, BigDecimal amount, String pin, HttpSession session);

    /**
     * Пополняет баланс кошелька текущего пользователя.
     *
     * @param session                текущая сессия пользователя.
     * @param walletUpdateBalanceDTO DTO с суммой пополнения.
     * @throws PaymentProcessingException если произошла ошибка при обработке платежа.
     */
    void deposit(HttpSession session, WalletUpdateBalanceDTO walletUpdateBalanceDTO);

    /**
     * Изменяет PIN-код кошелька пользователя.
     *
     * @param session            текущая сессия пользователя.
     * @param walletUpdatePinDTO DTO с новым и старым PIN-кодом.
     * @throws InvalidPinException если старый PIN-код неверный.
     */
    void changePin(HttpSession session, WalletUpdatePinDTO walletUpdatePinDTO);
}
