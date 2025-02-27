package ru.kalimulin.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.service.ImageService;

import java.util.List;

@RestController
@RequestMapping("/shop/images")
public class ImageController {
    private final ImageService imageService;
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<ImageResponseDTO> addImageToProduct(@PathVariable Long productId,
                                                              @RequestBody ImageCreateDTO imageCreateDTO) {
        logger.info("Добавление изображения к продукту с id {}", productId);
        ImageResponseDTO imageResponseDTO = imageService.addImageToProduct(productId, imageCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(imageResponseDTO);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ImageResponseDTO>> getImagesByProduct(@PathVariable Long productId) {
        logger.info("Запрос изображений для товара с id {}", productId);
        return ResponseEntity.ok(imageService.getImagesByProductId(productId));
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        logger.info("Удаление изображения с id {}", imageId);
        imageService.deleteImageById(imageId);
        return ResponseEntity.ok("Изображение успешно удалено");
    }
}