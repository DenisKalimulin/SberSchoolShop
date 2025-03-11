package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.kalimulin.customExceptions.categoryExceptions.CategoryNotFoundException;
import ru.kalimulin.customExceptions.imageExceptions.ImageLimitExceededException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UnauthorizedException;
import ru.kalimulin.customExceptions.userExceptions.UserIsNotSellerException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.dto.productDTO.ProductUpdateDTO;
import ru.kalimulin.enums.ProductStatus;
import ru.kalimulin.models.Product;

import java.math.BigDecimal;
import java.util.List;


/**
 * Сервис для управления товарами.
 */
public interface ProductService {

    /**
     * Находит товар по ID.
     *
     * @param id ID товара.
     * @return DTO товара.
     * @throws ProductNotFoundException если товар не найден.
     */
    ProductResponseDTO findById(Long id);

    /**
     * Получает все товары с пагинацией.
     *
     * @param pageable параметры пагинации.
     * @return страница DTO товаров.
     */
    Page<ProductResponseDTO> findAllProducts(Pageable pageable);

    /**
     * Поиск товаров по фильтрам.
     *
     * @param title    название товара (опционально).
     * @param category категория товара (опционально).
     * @param minPrice минимальная цена (опционально).
     * @param maxPrice максимальная цена (опционально).
     * @param pageable параметры пагинации.
     * @return страница DTO товаров.
     */
    Page<ProductResponseDTO> searchProducts(String title, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Получает список товаров продавца.
     *
     * @param sellerLogin логин продавца.
     * @param pageable    параметры пагинации.
     * @return страница DTO товаров.
     * @throws UserNotFoundException если продавец не найден.
     */
    Page<ProductResponseDTO> findAllBySeller(String sellerLogin, Pageable pageable);

    /**
     * Создает новый товар.
     *
     * @param productCreateDTO DTO с данными нового товара.
     * @param session          HTTP-сессия пользователя.
     * @return DTO созданного товара.
     * @throws UserNotFoundException       если пользователь не найден.
     * @throws UserIsNotSellerException    если пользователь не является продавцом.
     * @throws CategoryNotFoundException   если категория не найдена.
     * @throws ImageLimitExceededException если добавлено больше 5 изображений.
     */
    ProductResponseDTO createProduct(ProductCreateDTO productCreateDTO, HttpSession session);

    /**
     * Обновляет товар.
     *
     * @param id               ID товара.
     * @param productUpdateDTO DTO с обновленными данными.
     * @param session          HTTP-сессия пользователя.
     * @return обновленный DTO товара.
     * @throws ProductNotFoundException    если товар не найден.
     * @throws UnauthorizedException       если пользователь не является владельцем товара.
     * @throws ImageLimitExceededException если превышено количество изображений.
     */
    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO, HttpSession session);

    /**
     * Изменяет статус товара.
     *
     * @param id        ID товара.
     * @param newStatus новый статус.
     * @param session   HTTP-сессия пользователя.
     * @return обновленный DTO товара.
     * @throws ProductNotFoundException если товар не найден.
     * @throws UnauthorizedException    если пользователь не является владельцем товара.
     */
    ProductResponseDTO changeProductStatus(Long id, ProductStatus newStatus, HttpSession session);

    /**
     * Удаляет товар.
     *
     * @param id      ID товара.
     * @param session HTTP-сессия пользователя.
     * @throws ProductNotFoundException если товар не найден.
     * @throws UnauthorizedException    если пользователь не является владельцем товара.
     */
    void deleteProduct(Long id, HttpSession session);

    /**
     * Метод для получения топ-10 товаров, которые хранятся в кеше
     * @return топ-10 товаров
     */
    List<ProductResponseDTO> getPopularProducts();
}
