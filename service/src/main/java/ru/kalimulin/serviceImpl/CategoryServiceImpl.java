package ru.kalimulin.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kalimulin.customExceptions.categoryExceptions.CategoryAlreadyExistsException;
import ru.kalimulin.customExceptions.categoryExceptions.CategoryNotFoundException;
import ru.kalimulin.dto.categoryDTO.CategoryCreateDTO;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.dto.categoryDTO.CategoryUpdateDTO;
import ru.kalimulin.mappers.categoryMapper.CategoryMapper;
import ru.kalimulin.models.Category;
import ru.kalimulin.repositories.CategoryRepository;
import ru.kalimulin.service.CategoryService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Transactional(readOnly = true)
    @Override
    public CategoryResponseDTO findById(Long id) {
        logger.info("Поиск категории по ID");
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Категория не найдена", id);
                    return new CategoryNotFoundException("Категория c id " + id + " не найдена");
                });
        logger.info("Категория найдена: {}", category.getName());
        return categoryMapper.toCategoryResponseDTO(category);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponseDTO> findAll() {
        logger.info("Запрос на получение всех категорий");
        List<Category> categories = categoryRepository.findAll();
        logger.info("Найдено {} категорий", categories.size());
        return categoryMapper.toListCategoryResponseDTO(categories);
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryResponseDTO findByName(String name) {
        logger.info("Поиск категории по названию: {}", name);
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> {
                    logger.warn("Категория с названием '{}' не найдена", name);
                    return new CategoryNotFoundException("Категория c названием " + name + " не найдена");
                });

        logger.info("Категория найдена: {}", category.getName());
        return categoryMapper.toCategoryResponseDTO(category);
    }

    @Transactional
    @Override
    public CategoryResponseDTO createCategory(CategoryCreateDTO categoryCreateDTO) {
        logger.info("Попытка создания категории с названием: {}", categoryCreateDTO.getName());
        if (categoryRepository.existsByName(categoryCreateDTO.getName())) {
            logger.warn("Категория '{}' уже существует", categoryCreateDTO.getName());
            throw new CategoryAlreadyExistsException("Категория уже существует");
        }

        Category category = categoryMapper.toCategory(categoryCreateDTO);
        category = categoryRepository.save(category);

        logger.info("Категория '{}' успешно создана с ID: {}", category.getName(), category.getId());

        return categoryMapper.toCategoryResponseDTO(category);
    }

    @Transactional
    @Override
    public CategoryResponseDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO) {
        logger.info("Попытка обновления категории с ID: {}", categoryUpdateDTO.getId());

        if (categoryRepository.existsByName(categoryUpdateDTO.getName())) {
            logger.warn("Категория с названием '{}' уже существует", categoryUpdateDTO.getName());
            throw new CategoryAlreadyExistsException("Категория уже существует");
        }

        Category category = categoryRepository.findById(categoryUpdateDTO.getId())
                .orElseThrow(() -> {
                    logger.warn("Категория с ID {} не найдена", categoryUpdateDTO.getId());
                    return new CategoryNotFoundException("Категория не найдена");
                });

        if (categoryUpdateDTO.getName() != null) {
            logger.info("Обновление названия категории ID {} -> '{}'", category.getId(), categoryUpdateDTO.getName());
            category.setName(categoryUpdateDTO.getName());
        }

        category = categoryRepository.save(category);
        logger.info("Категория успешно обновлена: ID={}, Name={}", category.getId(), category.getName());
        return categoryMapper.toCategoryResponseDTO(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        logger.info("Попытка удаления категории с ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Категория с ID {} не найдена", id);
                    return new CategoryNotFoundException("Категория c id " + id + " не найдена");
                });

        categoryRepository.delete(category);
        logger.info("Категория с ID {} успешно удалена", id);
    }
}