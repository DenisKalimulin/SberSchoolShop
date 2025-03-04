package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.annotation.RoleRequired;
import ru.kalimulin.dto.categoryDTO.CategoryCreateDTO;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.dto.categoryDTO.CategoryUpdateDTO;
import ru.kalimulin.dto.userDTO.UserResponseDTO;
import ru.kalimulin.service.CategoryService;
import ru.kalimulin.service.ReviewService;
import ru.kalimulin.service.RoleService;
import ru.kalimulin.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/shop/admin")
@Tag(name = "Администратор", description = "Административные операции с пользователями, ролями, категориями и отзывами")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final RoleService roleService;
    private final ReviewService reviewService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/users/{id}")
    @RoleRequired("ADMIN")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID пользователя", example = "1", required = true)
            @PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Получить список всех пользователей",
            description = "Возвращает список всех зарегистрированных пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/users")
    @RoleRequired("ADMIN")
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Запрос на получение списка всех пользователей");
        List<UserResponseDTO> users = userService.getAllUsers();
        logger.info("Количество пользователей, полученных из базы данных: {}", users.size());
        return users;
    }

    @Operation(summary = "Назначить пользователю роль ADMIN", description = "Добавляет роль ADMIN пользователю по email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль ADMIN успешно назначена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping("/users/{userEmail}/roles")
    @RoleRequired("ADMIN")
    public ResponseEntity<UserResponseDTO> addRoleToUser(
            @Parameter(description = "Email пользователя", example = "user@example.com")
            @PathVariable String userEmail) {
        logger.info("Попытка назначения роли ADMIN пользователю");
        UserResponseDTO userResponseDTO = roleService.addAdminRole(userEmail);
        logger.info("Роль ADMIN успешно назначена пользователю");
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Создать новую категорию", description = "Добавляет новую категорию товаров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Категория успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping("/categories")
    @RoleRequired("ADMIN")
    public ResponseEntity<CategoryResponseDTO> addCategory(
            @Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        logger.info("Создание новой категории: {}", categoryCreateDTO.getName());
        CategoryResponseDTO categoryResponseDTO = categoryService.createCategory(categoryCreateDTO);

        logger.info("Категория {} успешно создана с ID {}", categoryResponseDTO.getName(), categoryResponseDTO.getId());
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @Operation(summary = "Обновить категорию", description = "Обновляет данные категории по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PutMapping("/categories/update")
    @RoleRequired("ADMIN")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        logger.info("Обновление категории: {}", categoryUpdateDTO.getName());
        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(categoryUpdateDTO);

        logger.info("Обновление категории с id {} прошло успешно ", categoryResponseDTO.getId());
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @Operation(summary = "Удалить категорию", description = "Удаляет категорию товаров по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @RoleRequired("ADMIN")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(
            @Parameter(description = "ID категории", example = "1")
            @PathVariable Long id) {
        categoryService.deleteCategory(id);

        logger.info("Категория с id {} уделена ", id);
        return ResponseEntity.ok("Категория удалена");
    }

    @Operation(summary = "Удалить отзыв", description = "Удаляет отзыв по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отзыв успешно удален"),
            @ApiResponse(responseCode = "404", description = "Отзыв не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @RoleRequired("ADMIN")
    @DeleteMapping("/review/{id}")
    public ResponseEntity<String> deleteReview(
            @Parameter(description = "ID категории", example = "1")
            @PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Отзыв удален");
    }
}