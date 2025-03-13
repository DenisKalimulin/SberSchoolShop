package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.service.RoleService;

@RestController
@RequestMapping("/shop/role")
@RequiredArgsConstructor
@Tag(name = "Роли", description = "Методы для управления родями пользователей")
public class RoleController {
    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Operation(summary = "Приобрести роль продавца", description = "Позволяет пользователю приобрести роль SELLER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль SELLER успешно приобретена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств для покупки роли")
    })
    @PostMapping("/seller")
    public ResponseEntity<UserResponseDTO> purchaseSellerRole(HttpSession session) {
        logger.info("Запрос на покупку роли BUYER");

        UserResponseDTO updatedUser = roleService.purchaseSellerRole(session);

        logger.info("Роль BUYER успешно приобретена");

        return ResponseEntity.ok(updatedUser);
    }
}
