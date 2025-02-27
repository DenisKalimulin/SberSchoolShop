package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.transferDTO.TransferRequestDTO;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdateBalanceDTO;
import ru.kalimulin.dto.walletDTO.WalletUpdatePinDTO;
import ru.kalimulin.service.WalletService;

@RestController
@RequestMapping("/shop/wallet")
public class WalletController {
    private final WalletService walletService;
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping
    public ResponseEntity<WalletResponseDTO> getUserBalanceAndWalletNumber(HttpSession session) {
        logger.info("Запрос на получение данных кошелька пользователя");
        WalletResponseDTO walletResponseDTO = walletService.getUserWallet(session);
        logger.info("Баланс: {}, Номер кошелька: {}", walletResponseDTO.getBalance(), walletResponseDTO.getWalletNumber());
        return ResponseEntity.ok(walletResponseDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<WalletResponseDTO> createWallet(HttpSession session,
                                                          @RequestBody WalletCreateDTO walletCreateDTO) {
        logger.info("Запрос на создание нового кошелька");
        WalletResponseDTO walletResponseDTO = walletService.createWallet(walletCreateDTO, session);
        logger.info("Кошелек успешно создан с номером {}", walletResponseDTO.getWalletNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(walletResponseDTO);
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositBalance(HttpSession session,
                                                 @RequestBody WalletUpdateBalanceDTO walletUpdateBalanceDTO) {
        logger.info("Запрос на пополнение баланса на сумму {}", walletUpdateBalanceDTO.getAmount());
        walletService.deposit(session, walletUpdateBalanceDTO);
        logger.info("Баланс успешно пополнен!");
        return ResponseEntity.ok("Баланс успешно пополнен!");
    }

    @PostMapping("/change-pin")
    public ResponseEntity<String> changePin(HttpSession session,
                                            @RequestBody WalletUpdatePinDTO walletUpdatePinDTO) {
        logger.info("Запрос на смену PIN-кода");
        walletService.changePin(session, walletUpdatePinDTO);
        logger.info("PIN-код успешно изменен!");
        return ResponseEntity.ok("Пин-код успешно изменен!");
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequestDTO transferDTO,
                                                HttpSession session) {
        logger.info("Запрос на перевод денег между кошельками");
        walletService.transfer(transferDTO.getWalletNumber(), transferDTO.getAmount(), transferDTO.getPin(), session);
        logger.info("Перевод прошел успешно");
        return ResponseEntity.ok("Перевод выполнен успешно!");
    }
}