package ru.kalimulin.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.dto.productDTO.ProductUpdateDTO;
import ru.kalimulin.enums.ProductStatus;
import ru.kalimulin.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/shop/products")
@RequiredArgsConstructor
@Tag(name = "Товары", description = "Методы для управления товарами")
public class ProductController {
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Operation(summary = "Добавить товар", description = "Позволяет продавцу добавить новый товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Товар успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductCreateDTO productCreateDTO,
                                                         HttpSession session) {
        logger.info("Запрос на добавление товара");
        ProductResponseDTO productResponseDTO = productService.createProduct(productCreateDTO, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }

    @Operation(summary = "Поиск товаров", description = "Позволяет искать товары по фильтрам: названию, категории и цене")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товары найдены")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProduct(
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "category") String category,
            @RequestParam(required = false, name = "minPrice") BigDecimal minPrice,
            @RequestParam(required = false, name = "maxPrice") BigDecimal maxPrice,
            Pageable pageable) {
        logger.info("Запрос на поиск товаров");
        Page<ProductResponseDTO> result = productService.searchProducts(title, category, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Найти товар по ID", description = "Возвращает информацию о товаре по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар найден"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProductById(
            @Parameter(description = "ID товара", example = "1")
            @PathVariable Long id) {
        logger.info("Запрос на поиск товара по id");
        ProductResponseDTO listingResponseDTO = productService.findById(id);

        return ResponseEntity.ok(listingResponseDTO);
    }

    @Operation(summary = "Получить все товары", description = "Возвращает список всех товаров с пагинацией")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список товаров получен")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAllProducts(Pageable pageable) {
        logger.info("Запрос на получение всех товаров");
        Page<ProductResponseDTO> result = productService.findAllProducts(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Получить товары продавца", description = "Возвращает список товаров, принадлежащих конкретному продавцу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список товаров продавца получен")
    })
    @GetMapping("/seller/{sellerLogin}")
    public ResponseEntity<Page<ProductResponseDTO>> findProductsBySeller(
            @Parameter(description = "Логин продавца", example = "seller123")
            @PathVariable String sellerLogin, Pageable pageable) {
        logger.info("Запрос на получение товаров продавца");
        Page<ProductResponseDTO> result = productService.findAllBySeller(sellerLogin, pageable);

        return ResponseEntity.ok(result);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно обновлён"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "ID товара", example = "1") @PathVariable Long id,
            @RequestBody ProductUpdateDTO productUpdateDTO,
            HttpSession session) {
        logger.info("Запрос на обновление товара");

        ProductResponseDTO updatedProduct = productService.updateProduct(id, productUpdateDTO, session);
        return ResponseEntity.ok(updatedProduct);
    }

    @Operation(summary = "Изменить статус товара", description = "Позволяет продавцу изменить статус товара (например, в продаже или продан)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус товара успешно изменён"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> changeStatus(
            @Parameter(description = "ID товара", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Новый статус товара")
            @RequestParam ProductStatus productStatus,
            HttpSession session) {
        logger.info("Запрос на смену статуса товара");
        ProductResponseDTO changeListingStatus = productService.changeProductStatus(id, productStatus, session);
        return ResponseEntity.ok(changeListingStatus);
    }

    @Operation(summary = "Удалить товар", description = "Позволяет продавцу удалить товар")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Товар успешно удалён"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @Parameter(description = "ID товара", example = "1") @PathVariable Long id,
            HttpSession session) {
        logger.info("Запрос на удаление товара");
        productService.deleteProduct(id, session);
        return ResponseEntity.ok("Товар успешно удалён");
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductResponseDTO>> getPopularProducts() {
        return ResponseEntity.ok(productService.getPopularProducts());
    }
}