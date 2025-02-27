package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.dto.userDTO.UserUpdateDTO;
import ru.kalimulin.service.UserService;
import ru.kalimulin.util.SessionUtils;

@RestController
@RequestMapping("/shop/users")
public class UserController {
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me/update")
    public ResponseEntity<UserResponseDTO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO,
                                                      HttpSession session) {

        UserResponseDTO updatedUser = userService.updateUser(session, userUpdateDTO);
        logger.info("Профиль пользователя с email {} был успешно обновлен", updatedUser.getEmail());

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteUser(HttpSession session) {

        userService.deleteUserByLogin(session);
        session.invalidate();
        logger.info("Пользователь с email {} был успешно удален из системы", SessionUtils.getUserLogin(session));

        return ResponseEntity.ok("Профиль удален");
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserResponseDTO> getUserByLogin(@PathVariable String login) {
        logger.info("Авторизация пользователя {} в систему", login);
        UserResponseDTO userResponseDTO = userService.getProfileByLogin(login);

        return ResponseEntity.ok(userResponseDTO);
    }
}