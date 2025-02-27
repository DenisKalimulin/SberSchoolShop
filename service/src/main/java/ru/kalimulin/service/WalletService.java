package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdateBalanceDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdatePinDTO;
import ru.kalimulin.models.User;

import java.math.BigDecimal;

public interface WalletService {
    WalletResponseDTO createWallet(WalletCreateDTO walletCreateDTO, HttpSession session);

    WalletResponseDTO getUserWallet(HttpSession session);

    void transfer(User buyer, User seller, BigDecimal amount);

    void transfer(String walletNumber, BigDecimal amount, String pin, HttpSession session);

    void deposit(HttpSession session, WalletUpdateBalanceDTO walletUpdateBalanceDTODTO);

    void changePin(HttpSession session, WalletUpdatePinDTO walletUpdatePinDTO);
}
