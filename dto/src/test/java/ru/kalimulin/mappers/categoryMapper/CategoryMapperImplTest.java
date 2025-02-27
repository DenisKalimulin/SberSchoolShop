package ru.kalimulin.mappers.categoryMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kalimulin.dto.categoryDTO.CategoryCreateDTO;
import ru.kalimulin.dto.categoryDTO.CategoryResponseDTO;
import ru.kalimulin.models.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperImplTest {

    private CategoryMapperImpl categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapperImpl();
    }

    @Test
    void toCategoryResponseDTO() {
        Category category = Category.builder()
                .id(1L)
                .name("Электроника")
                .build();

        CategoryResponseDTO responseDTO = categoryMapper.toCategoryResponseDTO(category);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getName()).isEqualTo("Электроника");
    }

    @Test
    void toCategory() {
        // Arrange
        CategoryCreateDTO createDTO = new CategoryCreateDTO();
        createDTO.setName("Электроника");

        // Act
        Category category = categoryMapper.toCategory(createDTO);

        // Assert
        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo("Электроника");
    }

    @Test
    void toCategoryResponseDTOList() {
        // Arrange
        Category category1 = Category.builder().id(1L).name("Электроника").build();
        Category category2 = Category.builder().id(2L).name("Аптека").build();
        List<Category> categories = List.of(category1, category2);

        // Act
        List<CategoryResponseDTO> responseDTOList = categoryMapper.toListCategoryResponseDTO(categories);

        // Assert
        assertThat(responseDTOList).isNotEmpty().hasSize(2);
        assertThat(responseDTOList.get(0).getId()).isEqualTo(1L);
        assertThat(responseDTOList.get(0).getName()).isEqualTo("Электроника");
        assertThat(responseDTOList.get(1).getId()).isEqualTo(2L);
        assertThat(responseDTOList.get(1).getName()).isEqualTo("Аптека");
    }

}
