package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.userDTO.LoginRequestDTO;
import ru.kalimulin.dto.userDTO.LoginResponseDTO;
import ru.kalimulin.dto.userDTO.UserRegistrationDTO;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.service.UserService;
import ru.kalimulin.util.SessionUtils;

@RestController
@RequestMapping("/shop/auth")
public class AuthController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody
                                                        UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO userResponseDTO = userService.registerUser(userRegistrationDTO);

        logger.info("Новый пользователь зарегистрирован: {}", userResponseDTO.getLogin());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody
                                                      LoginRequestDTO loginRequestDTO,
                                                      HttpSession session) {
        LoginResponseDTO loginResponseDTO = userService.authenticateUser(loginRequestDTO);
        session.setAttribute("userLogin", loginRequestDTO.getLogin());

        logger.info("Пользователь вошел в систему: {}", loginRequestDTO.getLogin());

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession session) {
        String login = SessionUtils.getUserLogin(session);

        session.invalidate();

        logger.info("Пользователь вышел из системы: {}", login);

        return ResponseEntity.ok("Успешный выход!");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(HttpSession session) {
        logger.info("Запрос на получение текущего профиля {}", SessionUtils.getUserLogin(session));
        UserResponseDTO userResponseDTO = userService.getThisUserProfile(session);
        return ResponseEntity.ok(userResponseDTO);
    }
}