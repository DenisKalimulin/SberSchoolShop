package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.service.RoleService;
import ru.kalimulin.util.SessionUtils;

@RestController
@RequestMapping("/shop/role")
public class RoleController {
    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/seller")
    public ResponseEntity<UserResponseDTO> buyer(HttpSession session) {
        logger.info("Запрос на покупку роли BUYER от пользователя {}", SessionUtils.getUserLogin(session));

        UserResponseDTO updatedUser = roleService.purchaseSellerRole(session);

        logger.info("Роль BUYER успешно приобретена пользователем {}", updatedUser.getLogin());

        return ResponseEntity.ok(updatedUser);
    }
}
