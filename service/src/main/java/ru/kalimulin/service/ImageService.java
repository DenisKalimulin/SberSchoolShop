package ru.kalimulin.service;

import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;

import java.util.List;

public interface ImageService {
    ImageResponseDTO addImageToProduct(Long productId, ImageCreateDTO imageCreateDTO);

    List<ImageResponseDTO> getImagesByProductId(Long productId);

    void deleteImageById(Long imageId);
}
