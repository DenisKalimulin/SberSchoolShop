package ru.kalimulin.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.dto.productDTO.ProductUpdateDTO;
import ru.kalimulin.enums.ProductStatus;
import ru.kalimulin.service.ProductService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/shop/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody ProductCreateDTO productCreateDTO,
                                                         HttpSession session) {
        ProductResponseDTO productResponseDTO = productService.createProduct(productCreateDTO, session);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponseDTO>> searchProduct(
            @RequestParam(required = false, name = "title") String title,
            @RequestParam(required = false, name = "category") String category,
            @RequestParam(required = false, name = "minPrice") BigDecimal minPrice,
            @RequestParam(required = false, name = "maxPrice") BigDecimal maxPrice,
            Pageable pageable) {
        Page<ProductResponseDTO> result = productService.searchProducts(title, category, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> findProductById(@PathVariable Long id) {
        ProductResponseDTO listingResponseDTO = productService.findById(id);

        return ResponseEntity.ok(listingResponseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> findAllProducts(Pageable pageable) {
        Page<ProductResponseDTO> result = productService.findAllProducts(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/seller/{sellerLogin}")
    public ResponseEntity<Page<ProductResponseDTO>> findProductsBySeller(@PathVariable String sellerLogin,
                                                                         Pageable pageable) {
        Page<ProductResponseDTO> result = productService.findAllBySeller(sellerLogin, pageable);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductUpdateDTO productUpdateDTO,
            HttpSession session) {

        ProductResponseDTO updatedProduct = productService.updateProduct(id, productUpdateDTO, session);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> changeStatus(@PathVariable Long id,
                                                           @RequestParam ProductStatus productStatus,
                                                           HttpSession session) {
        ProductResponseDTO changeListingStatus = productService.changeProductStatus(id, productStatus, session);
        return ResponseEntity.ok(changeListingStatus);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id,
                                                HttpSession session) {
        productService.deleteProduct(id, session);
        return ResponseEntity.ok("Товар успешно удален");
    }
}