package ru.kalimulin.serviceImpl;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.categoryExceptions.CategoryNotFoundException;
import ru.kalimulin.customExceptions.imageExceptions.ImageLimitExceededException;
import ru.kalimulin.customExceptions.productExceptions.ProductNotFoundException;
import ru.kalimulin.customExceptions.userExceptions.UnauthorizedException;
import ru.kalimulin.customExceptions.userExceptions.UserIsNotSellerException;
import ru.kalimulin.customExceptions.userExceptions.UserNotFoundException;
import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.dto.productDTO.ProductUpdateDTO;
import ru.kalimulin.enums.ProductStatus;
import ru.kalimulin.enums.RoleName;
import ru.kalimulin.mappers.imageMapper.ImageMapper;
import ru.kalimulin.mappers.productMapper.ProductMapper;
import ru.kalimulin.models.Category;
import ru.kalimulin.models.Image;
import ru.kalimulin.models.Product;
import ru.kalimulin.models.User;
import ru.kalimulin.repositories.CategoryRepository;
import ru.kalimulin.repositories.ProductRepository;
import ru.kalimulin.repositories.UserRepository;
import ru.kalimulin.service.ProductService;
import ru.kalimulin.specification.ProductSpecification;
import ru.kalimulin.util.SessionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageMapper imageMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(UserRepository userRepository, CategoryRepository categoryRepository,
                              ProductRepository productRepository, ProductMapper productMapper, ImageMapper imageMapper) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.imageMapper = imageMapper;
    }


    @Transactional
    @Override
    public ProductResponseDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Товар с id {} не найдено", id);
                    return new ProductNotFoundException("Товар с id " + id + " не найден");
                });

        return productMapper.toProductResponseDTO(product);
    }

    @Transactional
    @Override
    public Page<ProductResponseDTO> findAllProducts(Pageable pageable) {
        logger.info("Поиск всех объявлений с пагинацией {}", pageable);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toProductResponseDTO);
    }

    @Transactional
    @Override
    public Page<ProductResponseDTO> searchProducts(String title, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        logger.info("Поиск с параметрами: title={}, category={}, minPrice={}, maxPrice={}", title, category, minPrice, maxPrice);
        Specification<Product> specification = ProductSpecification.filterBy(title, category, minPrice, maxPrice);
        Page<Product> productPage = productRepository.findAll(specification, pageable);
        return productPage.map(productMapper::toProductResponseDTO);
    }

    @Transactional
    @Override
    public Page<ProductResponseDTO> findAllBySeller(String sellerLogin, Pageable pageable) {
        logger.info("Поиск объявлений пользователя");
        User seller = userRepository.findByLogin(sellerLogin)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден");
                    return new UserNotFoundException("Пользователя с таким логином не существует");
                });
        Page<Product> productPage = productRepository.findByOwner(seller, pageable);
        return productPage.map(productMapper::toProductResponseDTO);
    }

    @Transactional
    @Override
    public ProductResponseDTO createProduct(ProductCreateDTO productCreateDTO, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Создание нового товара");

        User user = userRepository.findByLogin(userLogin)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким логином " + userLogin + " не найден"));

        boolean isSeller = user.getRoles().stream().anyMatch(role -> role.getRoleName() == RoleName.SELLER);

        if (!isSeller) {
            logger.error("Пользователь не является продавцом");
            throw new UserIsNotSellerException("Пользователь с логином " + user.getLogin() + " не является продавцом");
        }

        Category category = categoryRepository.findById(productCreateDTO.getCategoryId())
                .orElseThrow(() -> {
                    logger.error("Категория с id {} не найдена", productCreateDTO.getCategoryId());
                    return new CategoryNotFoundException("Категория с таким id " + productCreateDTO.getCategoryId() + " не найдена");
                });

        Product product = productMapper.toProduct(productCreateDTO);
        product.setCategory(category);
        product.setOwner(user);

        if (productCreateDTO.getImages() != null) {
            if (productCreateDTO.getImages().size() > 5) {
                logger.error("Попытка загрузить более 5 изображений");
                throw new ImageLimitExceededException("Нельзя загрузить более 5 изображений");
            }
        }

        List<Image> images = productCreateDTO.getImages().stream()
                .map(imageDTO -> imageMapper.toImage(imageDTO))
                .collect(Collectors.toList());
        product.setImages(images);

        product = productRepository.save(product);

        logger.info("Товар с названием '{}' успешно создан", product.getTitle());
        return productMapper.toProductResponseDTO(product);
    }

    @Transactional
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Обновление данных в товаре с id {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Объявление с id {} не найдено", id);
                    return new ProductNotFoundException("Товар с id " + id + " не найден");
                });

        if (!product.getOwner().getLogin().equalsIgnoreCase(userLogin)) {
            logger.warn("Не авторизованный пользователь пытается изменить товар с id {}", id);
            throw new UnauthorizedException("Вы не можете изменить это объявление");
        }

        if (productUpdateDTO.getTitle() != null) {
            product.setTitle(productUpdateDTO.getTitle());
        }
        if (productUpdateDTO.getDescription() != null) {
            product.setDescription(productUpdateDTO.getDescription());
        }
        if (productUpdateDTO.getPrice() != null) {
            product.setPrice(productUpdateDTO.getPrice());
        }
        if (productUpdateDTO.getStocks() != null) {
            product.setStocks(productUpdateDTO.getStocks());
        }
        if (productUpdateDTO.getImageUrls() != null && !productUpdateDTO.getImageUrls().isEmpty()) {
            if (productUpdateDTO.getImageUrls().size() > 5) {
                logger.warn("Попытка загрузить более 5 изображений для товара с id {}", id);
                throw new ImageLimitExceededException("Нельзя загрузить более 5 изображений");
            }
            product.getImages().clear();
            List<Image> newImages = productUpdateDTO.getImageUrls().stream()
                    .map(url -> Image.builder().imageUrl(url).product(product).build())
                    .collect(Collectors.toList());
            product.getImages().addAll(newImages);
        }
        if (productUpdateDTO.getCategoryId() != null) {
            Category newCategory = categoryRepository.findById(productUpdateDTO.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Категория с id {} не найдена", productUpdateDTO.getCategoryId());
                        return new CategoryNotFoundException("Категория с id " + productUpdateDTO.getCategoryId() + " не найдена");
                    });

            product.setCategory(newCategory);
        }
        productRepository.save(product);
        logger.info("Товар с id {} успешно обновлен", id);

        return productMapper.toProductResponseDTO(product);
    }

    @Transactional
    @Override
    public ProductResponseDTO changeProductStatus(Long id, ProductStatus newStatus, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Попытка смены статуса у объявления с id {} на статус {}", id, newStatus);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Объявление с id {} не найдено", id);
                    return new ProductNotFoundException("Товар не найден");
                });

        if (!product.getOwner().getLogin().equalsIgnoreCase(userLogin)) {
            logger.warn("Не авторизованный пользователь пытается изменить статус объявления с id {}", id);
            throw new UnauthorizedException("Вы не можете изменить статус в этом объявлении");
        }

        product.setStatus(newStatus);
        productRepository.save(product);
        logger.info("Статус объявления с id {} изменен на {}", id, newStatus);
        return productMapper.toProductResponseDTO(product);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id, HttpSession session) {
        String userLogin = SessionUtils.getUserLogin(session);
        logger.info("Удаление товара с id {} пользователем с логином", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Товар с  id {} не найден", id);
                    return new ProductNotFoundException("Объявление не найдено");
                });

        if (!product.getOwner().getLogin().equalsIgnoreCase(userLogin)) {
            logger.warn("Не авторизованный пользователь пытается изменить статус объявления с id {}", id);
            throw new UnauthorizedException("Вы не можете изменить статус в этом объявлении");
        }
        productRepository.delete(product);
        logger.info("Товар с id {} успешно удален", id);
    }
}