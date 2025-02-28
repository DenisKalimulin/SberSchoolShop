package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
@Tag(name = "Кошелек", description = "Методы для работы с кошельком пользователя")
public class WalletController {
    private final WalletService walletService;
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Operation(summary = "Получить данные кошелька", description = "Возвращает баланс и номер кошелька пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные кошелька успешно получены"),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден")
    })
    @GetMapping
    public ResponseEntity<WalletResponseDTO> getUserBalanceAndWalletNumber(HttpSession session) {
        logger.info("Запрос на получение данных кошелька пользователя");
        WalletResponseDTO walletResponseDTO = walletService.getUserWallet(session);
        logger.info("Баланс: {}, Номер кошелька: {}", walletResponseDTO.getBalance(), walletResponseDTO.getWalletNumber());
        return ResponseEntity.ok(walletResponseDTO);
    }


    @Operation(summary = "Создать кошелек", description = "Создает новый кошелек для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Кошелек успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "409", description = "У пользователя уже есть кошелек")
    })
    @PostMapping("/create")
    public ResponseEntity<WalletResponseDTO> createWallet(HttpSession session,
                                                          @RequestBody WalletCreateDTO walletCreateDTO) {
        logger.info("Запрос на создание нового кошелька");
        WalletResponseDTO walletResponseDTO = walletService.createWallet(walletCreateDTO, session);
        logger.info("Кошелек успешно создан с номером {}", walletResponseDTO.getWalletNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(walletResponseDTO);
    }

    @Operation(summary = "Пополнить баланс", description = "Пополняет баланс кошелька пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс успешно пополнен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден")
    })
    @PostMapping("/deposit")
    public ResponseEntity<String> depositBalance(HttpSession session,
                                                 @RequestBody WalletUpdateBalanceDTO walletUpdateBalanceDTO) {
        logger.info("Запрос на пополнение баланса на сумму {}", walletUpdateBalanceDTO.getAmount());
        walletService.deposit(session, walletUpdateBalanceDTO);
        logger.info("Баланс успешно пополнен!");
        return ResponseEntity.ok("Баланс успешно пополнен!");
    }

    @Operation(summary = "Сменить PIN-код", description = "Изменяет PIN-код кошелька пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PIN-код успешно изменен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден")
    })
    @PostMapping("/change-pin")
    public ResponseEntity<String> changePin(HttpSession session,
                                            @RequestBody WalletUpdatePinDTO walletUpdatePinDTO) {
        logger.info("Запрос на смену PIN-кода");
        walletService.changePin(session, walletUpdatePinDTO);
        logger.info("PIN-код успешно изменен!");
        return ResponseEntity.ok("Пин-код успешно изменен!");
    }

    @Operation(summary = "Перевести деньги", description = "Переводит деньги между кошельками пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "403", description = "Неверный PIN-код"),
            @ApiResponse(responseCode = "404", description = "Кошелек отправителя или получателя не найден"),
            @ApiResponse(responseCode = "409", description = "Недостаточно средств")
    })
    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequestDTO transferDTO,
                                                HttpSession session) {
        logger.info("Запрос на перевод денег между кошельками");
        walletService.transfer(transferDTO.getWalletNumber(), transferDTO.getAmount(), transferDTO.getPin(), session);
        logger.info("Перевод прошел успешно");
        return ResponseEntity.ok("Перевод выполнен успешно!");
    }
}