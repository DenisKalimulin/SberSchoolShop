package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.productDTO.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.kalimulin.enums.ProductStatus;

import java.math.BigDecimal;


public interface ProductService {
    ProductResponseDTO findById(Long id);

    Page<ProductResponseDTO> findAllProducts(Pageable pageable);

    Page<ProductResponseDTO> searchProducts(String title, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<ProductResponseDTO> findAllBySeller(String sellerLogin, Pageable pageable);

    ProductResponseDTO createProduct(ProductCreateDTO productCreateDTO, HttpSession session);

    ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO, HttpSession session);

    ProductResponseDTO changeProductStatus(Long id, ProductStatus newStatus, HttpSession session);

    void deleteProduct(Long id, HttpSession session);

}
