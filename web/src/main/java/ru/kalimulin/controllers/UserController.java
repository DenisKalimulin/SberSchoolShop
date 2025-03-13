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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.dto.userDTO.UserUpdateDTO;
import ru.kalimulin.service.UserService;

@RestController
@RequestMapping("/shop/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Методы для управления пользователями")
public class UserController {
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Обновить профиль пользователя", description = "Позволяет текущему пользователю обновить свой профиль")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    @PutMapping("/me/update")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                                      HttpSession session) {

        UserResponseDTO updatedUser = userService.updateUser(session, userUpdateDTO);
        logger.info("Профиль пользователя был успешно обновлен");

        return ResponseEntity.ok(updatedUser);
    }


    @Operation(summary = "Удалить пользователя", description = "Удаляет текущего пользователя из системы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Профиль успешно удален"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteUser(HttpSession session) {

        userService.deleteUserByLogin(session);
        session.invalidate();
        logger.info("Пользователь был успешно удален из системы");

        return ResponseEntity.ok("Профиль удален");
    }

    @Operation(summary = "Получить пользователя по логину", description = "Возвращает профиль пользователя по логину")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{login}")
    public ResponseEntity<UserResponseDTO> getUserByLogin(@PathVariable String login) {
        logger.info("Авторизация пользователя в систему");
        UserResponseDTO userResponseDTO = userService.getProfileByLogin(login);

        return ResponseEntity.ok(userResponseDTO);
    }
}