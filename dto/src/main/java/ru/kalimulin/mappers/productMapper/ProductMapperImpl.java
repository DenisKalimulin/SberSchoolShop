package ru.kalimulin.mappers.productMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.mappers.imageMapper.ImageMapper;
import ru.kalimulin.models.Product;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapperImpl implements ProductMapper {
    private final ImageMapper imageMapper;

    @Autowired
    public ProductMapperImpl(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    @Override
    public ProductResponseDTO toProductResponseDTO(Product product) {
        if (product == null) {
            return null;
        }

        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .stocks(product.getStocks())
                .sellerEmail(product.getOwner().getEmail())
                .categoryName(product.getCategory().getName())
                .status(product.getStatus())
                .images(imageMapper.toListImageDTO(product.getImages()))
                .build();

    }

    @Override
    public Product toProduct(ProductCreateDTO productCreateDTO) {
        if (productCreateDTO == null) {
            return null;
        }

        return Product.builder()
                .title(productCreateDTO.getTitle())
                .description(productCreateDTO.getDescription())
                .price(productCreateDTO.getPrice())
                .stocks(productCreateDTO.getStocks())
                .status(productCreateDTO.getStatus())
                .images(productCreateDTO.getImages().stream()
                        .map(imageMapper::toImage)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public List<ProductResponseDTO> toListProductResponseDTO(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }

        return products.stream()
                .map(this::toProductResponseDTO)
                .collect(Collectors.toList());
    }
}