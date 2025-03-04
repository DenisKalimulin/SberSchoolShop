package ru.kalimulin.service;

import ru.kalimulin.customExceptions.imageExceptions.ImageLimitExceededException;
import ru.kalimulin.customExceptions.imageExceptions.ImageNotFoundException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;

import java.util.List;

/**
 * Сервис для управления изображениями товаров
 */
public interface ImageService {

    /**
     * Добавляет изображение к товару.
     *
     * @param productId      ID товара, к которому добавляется изображение.
     * @param imageCreateDTO DTO с данными изображения.
     * @return DTO с информацией о добавленном изображении.
     * @throws ProductNotFoundException    если товар не найден.
     * @throws ImageLimitExceededException если превышено максимальное количество изображений (5).
     */
    ImageResponseDTO addImageToProduct(Long productId, ImageCreateDTO imageCreateDTO);

    /**
     * Получает список изображений для указанного товара.
     *
     * @param productId ID товара.
     * @return Список DTO изображений.
     */
    List<ImageResponseDTO> getImagesByProductId(Long productId);

    /**
     * Удаляет изображение по ID.
     *
     * @param imageId ID изображения.
     * @throws ImageNotFoundException если изображение не найдено.
     */
    void deleteImageById(Long imageId);
}