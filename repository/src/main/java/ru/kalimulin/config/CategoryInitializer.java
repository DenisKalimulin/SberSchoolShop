package ru.kalimulin.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kalimulin.models.Category;
import ru.kalimulin.repositories.CategoryRepository;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CategoryInitializer implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(CategoryInitializer.class);

    @Autowired
    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> defaultCategories = Arrays.asList("Электроника", "Аптека", "Строительство и ремонт",
                "Автотовары", "Хобби и творчество", "Товары для животных",
                "Одежда", "Книги", "Игрушки", "Бытовая техника");

        for (String categoryName : defaultCategories) {
            if (!categoryRepository.existsByName(categoryName)) {
                Category category = new Category();
                category.setName(categoryName);
                categoryRepository.save(category);
                logger.info("Добавлена категория: {}", categoryName);
            } else {
                logger.debug("Категория уже существует: {}", categoryName);
            }
        }
    }
}