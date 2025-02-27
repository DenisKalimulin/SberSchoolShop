package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final RoleService roleService;
    private final ReviewService reviewService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AdminController(UserService userService, CategoryService categoryService,
                           RoleService roleService, ReviewService reviewService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.roleService = roleService;
        this.reviewService = reviewService;
    }

    @GetMapping("/users/{id}")
    @RoleRequired("ADMIN")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    @RoleRequired("ADMIN")
    public List<UserResponseDTO> getAllUsers() {
        logger.info("Запрос на получение списка всех пользователей");
        List<UserResponseDTO> users = userService.getAllUsers();
        logger.info("Количество пользователей, полученных из базы данных: {}", users.size());
        return users;
    }

    @PostMapping("/users/{userEmail}/roles")
    @RoleRequired("ADMIN")
    public ResponseEntity<UserResponseDTO> addRoleToUser(@PathVariable String userEmail) {
        logger.info("Попытка назначения роли ADMIN пользователю с email: {}", userEmail);
        UserResponseDTO userResponseDTO = roleService.addAdminRole(userEmail);
        logger.info("Роль ADMIN успешно назначена пользователю с email: {}", userEmail);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping("/categories")
    @RoleRequired("ADMIN")
    public ResponseEntity<CategoryResponseDTO> addCategory(
            @Valid @RequestBody CategoryCreateDTO categoryCreateDTO) {
        logger.info("Создание новой категории: {}", categoryCreateDTO.getName());
        CategoryResponseDTO categoryResponseDTO = categoryService.createCategory(categoryCreateDTO);

        logger.info("Категория {} успешно создана с ID {}", categoryResponseDTO.getName(), categoryResponseDTO.getId());
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @PutMapping("/categories/update")
    @RoleRequired("ADMIN")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        logger.info("Обновление категории: {}", categoryUpdateDTO.getName());
        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(categoryUpdateDTO);

        logger.info("Обновление категории с id {} прошло успешно ", categoryResponseDTO.getId());
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @RoleRequired("ADMIN")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        logger.info("Категория с id {} уделена ", id);
        return ResponseEntity.ok("Категория удалена");
    }

    @RoleRequired("ADMIN")
    @DeleteMapping("/review/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Отзыв удален");
    }
}