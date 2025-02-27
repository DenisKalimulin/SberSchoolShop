package ru.kalimulin.service;

import jakarta.servlet.http.HttpSession;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;

import java.util.List;

public interface FavoriteService {
    void addToFavorite(Long productId, HttpSession session);

    void removeFromFavorites(Long productId, HttpSession session);

    List<ProductResponseDTO> getFavorites(HttpSession session);
}
