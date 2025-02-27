package ru.kalimulin.mappers.productMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.dto.productDTO.ProductCreateDTO;
import ru.kalimulin.dto.productDTO.ProductResponseDTO;
import ru.kalimulin.enums.ProductStatus;
import ru.kalimulin.mappers.imageMapper.ImageMapper;
import ru.kalimulin.models.Category;
import ru.kalimulin.models.Image;
import ru.kalimulin.models.Product;
import ru.kalimulin.models.User;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ProductMapperImplTest {

    private ProductMapperImpl productMapper;
    private ImageMapper imageMapper;

    @BeforeEach
    void setUp() {
        imageMapper = Mockito.mock(ImageMapper.class);
        productMapper = new ProductMapperImpl(imageMapper);
    }

    @Test
    void toProductResponseDTO() {
        // Arrange
        User user = User.builder().email("seller@example.com").build();
        Category category = Category.builder().name("Электроника").build();
        Image image = Image.builder().id(1L).imageUrl("http://example.com/image.jpg").build();

        Product product = Product.builder()
                .id(10L)
                .title("Iphone 12")
                .description("new")
                .price(new BigDecimal("20000"))
                .stocks(5)
                .status(ProductStatus.AVAILABLE)
                .owner(user)
                .category(category)
                .images(List.of(image))
                .build();

        ImageResponseDTO imageResponseDTO = new ImageResponseDTO(1L, "http://example.com/image.jpg");
        when(imageMapper.toListImageDTO(List.of(image))).thenReturn(List.of(imageResponseDTO));

        ProductResponseDTO responseDTO = productMapper.toProductResponseDTO(product);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(10L);
        assertThat(responseDTO.getTitle()).isEqualTo("Iphone 12");
        assertThat(responseDTO.getDescription()).isEqualTo("new");
        assertThat(responseDTO.getPrice()).isEqualTo(new BigDecimal("20000"));
        assertThat(responseDTO.getStocks()).isEqualTo(5);
        assertThat(responseDTO.getSellerEmail()).isEqualTo("seller@example.com");
        assertThat(responseDTO.getCategoryName()).isEqualTo("Электроника");
        assertThat(responseDTO.getStatus()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(responseDTO.getImages()).hasSize(1);
        assertThat(responseDTO.getImages().get(0).getImageUrl()).isEqualTo("http://example.com/image.jpg");
    }

    @Test
    void toProduct() {
        ImageCreateDTO imageCreateDTO = new ImageCreateDTO(1L, "http://example.com/image.jpg");
        ProductCreateDTO createDTO = ProductCreateDTO.builder()
                .title("Smartphone")
                .description("New")
                .price(new BigDecimal("8000"))
                .stocks(10)
                .status(ProductStatus.AVAILABLE)
                .images(List.of(imageCreateDTO))
                .build();

        Image image = Image.builder().imageUrl("http://example.com/image.jpg").build();
        when(imageMapper.toImage(imageCreateDTO)).thenReturn(image);

        Product product = productMapper.toProduct(createDTO);

        assertThat(product).isNotNull();
        assertThat(product.getTitle()).isEqualTo("Smartphone");
        assertThat(product.getDescription()).isEqualTo("New");
        assertThat(product.getPrice()).isEqualTo(new BigDecimal("8000"));
        assertThat(product.getStocks()).isEqualTo(10);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(product.getImages()).hasSize(1);
        assertThat(product.getImages().get(0).getImageUrl()).isEqualTo("http://example.com/image.jpg");
    }

    @Test
    void toProductResponseDTOList() {
        User user = User.builder().email("seller@example.com").build();
        Category category = Category.builder().name("Аптека").build();
        Image image = Image.builder().id(2L).imageUrl("http://example.com/img2.jpg").build();

        Product product1 = Product.builder()
                .id(101L)
                .title("Активированный уголь")
                .description("уголь")
                .price(new BigDecimal("50"))
                .stocks(3)
                .status(ProductStatus.AVAILABLE)
                .owner(user)
                .category(category)
                .images(List.of(image))
                .build();

        Product product2 = Product.builder()
                .id(102L)
                .title("Терафлю")
                .description("От простуды")
                .price(new BigDecimal("500"))
                .stocks(2)
                .status(ProductStatus.OUT_OF_STOCK)
                .owner(user)
                .category(category)
                .images(List.of(image))
                .build();

        ImageResponseDTO imageResponseDTO = new ImageResponseDTO(2L, "http://example.com/img2.jpg");
        when(imageMapper.toListImageDTO(List.of(image))).thenReturn(List.of(imageResponseDTO));

        List<ProductResponseDTO> responseDTOList = productMapper.toListProductResponseDTO(List.of(product1, product2));

        assertThat(responseDTOList).hasSize(2);
        assertThat(responseDTOList.get(0).getTitle()).isEqualTo("Активированный уголь");
        assertThat(responseDTOList.get(1).getTitle()).isEqualTo("Терафлю");
        assertThat(responseDTOList.get(0).getStatus()).isEqualTo(ProductStatus.AVAILABLE);
        assertThat(responseDTOList.get(1).getStatus()).isEqualTo(ProductStatus.OUT_OF_STOCK);
    }
}

