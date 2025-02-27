package ru.kalimulin.mappers.imageMapper;

import org.springframework.stereotype.Component;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.models.Image;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ImageMapperImpl implements ImageMapper {
    @Override
    public ImageResponseDTO toImageResponseDTO(Image image) {
        if (image == null) {
            return null;
        }

        return ImageResponseDTO.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .build();
    }

    @Override
    public Image toImage(ImageCreateDTO imageCreateDTO) {
        if (imageCreateDTO == null) {
            return null;
        }

        return Image.builder()
                .imageUrl(imageCreateDTO.getImageUrl())
                .build();
    }

    @Override
    public List<ImageResponseDTO> toListImageDTO(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        return images.stream()
                .map(this::toImageResponseDTO)
                .collect(Collectors.toList());
    }
}