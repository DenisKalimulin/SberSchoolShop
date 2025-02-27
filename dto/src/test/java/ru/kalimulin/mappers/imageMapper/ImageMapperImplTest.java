package ru.kalimulin.mappers.imageMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kalimulin.dto.imageDTO.ImageCreateDTO;
import ru.kalimulin.dto.imageDTO.ImageResponseDTO;
import ru.kalimulin.models.Image;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ImageMapperImplTest {

    private ImageMapperImpl imageMapper;

    @BeforeEach
    void setUp() {
        imageMapper = new ImageMapperImpl();
    }

    @Test
    void toImageResponseDTOTest() {
        Image image = Image.builder()
                .id(1L)
                .imageUrl("http://example.com/image1.jpg")
                .build();

        ImageResponseDTO responseDTO = imageMapper.toImageResponseDTO(image);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getId()).isEqualTo(1L);
        assertThat(responseDTO.getImageUrl()).isEqualTo("http://example.com/image1.jpg");
    }

    @Test
    void toImageTest() {
        ImageCreateDTO createDTO = new ImageCreateDTO();
        createDTO.setImageUrl("http://example.com/image2.jpg");

        Image image = imageMapper.toImage(createDTO);

        assertThat(image).isNotNull();
        assertThat(image.getImageUrl()).isEqualTo("http://example.com/image2.jpg");
    }

    @Test
    void toImageResponseDTOList() {
        Image image1 = Image.builder().id(1L).imageUrl("http://example.com/img1.jpg").build();
        Image image2 = Image.builder().id(2L).imageUrl("http://example.com/img2.jpg").build();
        List<Image> images = List.of(image1, image2);

        List<ImageResponseDTO> responseDTOList = imageMapper.toListImageDTO(images);

        assertThat(responseDTOList).isNotEmpty().hasSize(2);
        assertThat(responseDTOList.get(0).getId()).isEqualTo(1L);
        assertThat(responseDTOList.get(0).getImageUrl()).isEqualTo("http://example.com/img1.jpg");
        assertThat(responseDTOList.get(1).getId()).isEqualTo(2L);
        assertThat(responseDTOList.get(1).getImageUrl()).isEqualTo("http://example.com/img2.jpg");
    }
}

