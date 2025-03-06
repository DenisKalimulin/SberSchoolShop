package ru.kalimulin.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.imageExceptions.ImageLimitExceededException;
import ru.kalimulin.customExceptions.imageExceptions.ImageNotFoundException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.mappers.imageMapper.ImageMapper;
import ru.kalimulin.models.Image;
import ru.kalimulin.models.Product;
import ru.kalimulin.repositories.ImageRepository;
import ru.kalimulin.repositories.ProductRepository;
import ru.kalimulin.service.ImageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ImageMapper imageMapper;
    private static final Logger logger = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Transactional
    @Override
    public ImageResponseDTO addImageToProduct(Long productId, ImageCreateDTO imageCreateDTO) {
        logger.info("Начало добавления изображения к товару с id {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.error("Продукт с ID {} не найден!", productId);
                    return new ProductNotFoundException("Продукт не найден");
                });

        if (product.getImages().size() >= 5) {
            logger.warn("Превышен лимит изображений для товара ID {}", productId);
            throw new ImageLimitExceededException("У товара не может быть больше 5 изображений");
        }

        Image image = Image.builder()
                .imageUrl(imageCreateDTO.getImageUrl())
                .product(product)
                .build();

        Image savedImage = imageRepository.save(image);
        logger.info("Изображение добавлено в базу: {}", savedImage.getImageUrl());

        return imageMapper.toImageResponseDTO(savedImage);
    }

    @Transactional
    @Override
    public List<ImageResponseDTO> getImagesByProductId(Long productId) {
        List<Image> images = imageRepository.findByProductId(productId);
        return imageMapper.toListImageDTO(images);
    }

    @Transactional
    @Override
    public void deleteImageById(Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ImageNotFoundException("Изображение с id " + imageId + " не найдено");
        }
        imageRepository.deleteById(imageId);
    }
}
