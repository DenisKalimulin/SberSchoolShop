package ru.kalimulin.mappers.imageMapper;

import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.models.Image;

import java.util.List;

public interface ImageMapper {
    ImageResponseDTO toImageResponseDTO(Image image);

    Image toImage(ImageCreateDTO imageCreateDTO);

    List<ImageResponseDTO> toListImageDTO(List<Image> images);

}
