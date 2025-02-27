package ru.kalimulin.mappers.productMapper;

import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.models.Product;

import java.util.List;

public interface ProductMapper {
    ProductResponseDTO toProductResponseDTO(Product product);

    Product toProduct(ProductCreateDTO productCreateDTO);

    List<ProductResponseDTO> toListProductResponseDTO(List<Product> products);
}
