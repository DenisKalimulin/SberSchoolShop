package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "Методы для регистрации, входа и выхода")
public class AuthController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации запроса")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody
                                                        UserRegistrationDTO userRegistrationDTO) {
        UserResponseDTO userResponseDTO = userService.registerUser(userRegistrationDTO);

        logger.info("Новый пользователь зарегистрирован: {}", userResponseDTO.getLogin());

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);

    }

    @Operation(summary = "Вход в систему", description = "Аутентифицирует пользователя и создает сессию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход в систему"),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody
                                                      LoginRequestDTO loginRequestDTO,
                                                      HttpSession session) {
        LoginResponseDTO loginResponseDTO = userService.authenticateUser(loginRequestDTO);
        session.setAttribute("userLogin", loginRequestDTO.getLogin());

        logger.info("Пользователь вошел в систему: {}", loginRequestDTO.getLogin());

        return ResponseEntity.ok(loginResponseDTO);
    }

    @Operation(summary = "Выход из системы", description = "Удаляет сессию пользователя и выходит из аккаунта")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход из системы")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession session) {
        String login = SessionUtils.getUserLogin(session);

        session.invalidate();

        logger.info("Пользователь вышел из системы: {}", login);

        return ResponseEntity.ok("Успешный выход!");
    }

    @Operation(summary = "Получение профиля пользователя", description = "Возвращает профиль текущего авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль пользователя получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(HttpSession session) {
        logger.info("Запрос на получение текущего профиля {}", SessionUtils.getUserLogin(session));
        UserResponseDTO userResponseDTO = userService.getThisUserProfile(session);
        return ResponseEntity.ok(userResponseDTO);
    }
}