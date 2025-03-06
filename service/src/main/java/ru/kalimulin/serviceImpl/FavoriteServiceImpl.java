package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.favoriteExceptions.FavoriteNotFoundException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.mappers.productMapper.ProductMapper;
import ru.kalimulin.models.Favorite;
import ru.kalimulin.models.Product;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.FavoriteRepository;
import ru.kalimulin.repositories.ProductRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.service.FavoriteService;
import ru.kalimulin.util.SessionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    @Transactional
    @Override
    public void addToFavorite(Long productId, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);

        User user = findUserByLogin(userLogin);

        Product product = findProductById(productId);

        logger.info("Пользователь пытается добавить в избранное товар с id {}", productId);

        Favorite favorite = favoriteRepository.findByUser(user)
                .orElse(Favorite.builder().user(user).products(new ArrayList<>()).build());

        if (favorite.getProducts().contains(product)) {
            logger.warn("Попытка добавить товар которое уже есть в избранном");
            throw new IllegalStateException("Товар уже в избранном");
        }

        favorite.getProducts().add(product);
        favoriteRepository.save(favorite);
        logger.info("Пользователь успешно добавил в избранное товар с id {}", productId);
    }

    @Transactional
    @Override
    public void removeFromFavorites(Long productId, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        User user = findUserByLogin(userLogin);

        Product product = findProductById(productId);

        logger.info("Попытка пользователя удалить товар из избранного");

        Favorite favorite = findFavoriteByUser(user);

        favorite.getProducts().remove(product);
        favoriteRepository.save(favorite);

        logger.info("Пользователь успешно удалил товар с id {}", productId);
    }

    @Transactional
    @Override
    public List<ProductResponseDTO> getFavorites(HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        User user = findUserByLogin(userLogin);

        logger.info("Попытка получить список товаров из избранного пользователя");

        Favorite favorite = findFavoriteByUser(user);

        logger.info("Пользователь получил список товаров из избранного");
        return productMapper.toListProductResponseDTO(favorite.getProducts());
    }

    /**
     * Получение пользователя по логину.
     *
     * @param login логин пользователя.
     * @return найденный пользователь.
     * @throws UserNotFoundException если пользователь с таким логином не найден.
     */
    private User findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> {
                    logger.warn("Пользователь не найден");
                    return new UserNotFoundException("Пользователь с таким логином " + login + " не найден");
                });
    }

    /**
     * Получение товара по id.
     *
     * @param productId id товара
     * @return найденный товар
     * @throws ProductNotFoundException если товар с таким id не найден.
     */
    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> {
                    logger.warn("Товар с id " + productId + " не найден");
                    return new ProductNotFoundException("Товар с id " + productId + " не найден");
                });
    }

    /**
     * Получение избранного пользователя.
     *
     * @param user пользователь для которого возвращается его избранное.
     * @return избранное
     * @throws FavoriteNotFoundException если избранное не найдено
     */
    private Favorite findFavoriteByUser(User user) {
        return favoriteRepository.findByUser(user)
                .orElseThrow(() -> {
                    logger.warn("Избранное пользователя не найдено");
                    return new FavoriteNotFoundException("Избранное не найдено");
                });
    }
}
