package ru.kalimulin.mappers.addressMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kalimulin.dto.addressDTO.AddressCreateDTO;
import ru.kalimulin.dto.addressDTO.AddressResponseDTO;
import ru.kalimulin.models.Address;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AddressMapperImplTest {
    private AddressMapperImpl addressMapper;

    @BeforeEach
    void setApp() {
        addressMapper = new AddressMapperImpl();
    }

    @Test
    void toAddressResponseDTO() {
        Address address = Address.builder()
                .country("Россия")
                .region("Ростовская область")
                .city("Ростов-на-Дону")
                .street("Доватора 123")
                .zipCode(344041)
                .build();

        AddressResponseDTO result = addressMapper.toAddressResponseDTO(address);

        assertThat(result).isNotNull();
        assertThat(result.getCountry()).isEqualTo(address.getCountry());
        assertThat(result.getRegion()).isEqualTo(address.getRegion());
        assertThat(result.getCity()).isEqualTo(address.getCity());
        assertThat(result.getStreet()).isEqualTo(address.getStreet());
        assertThat(result.getZipCode()).isEqualTo(address.getZipCode());
    }

    @Test
    void toAddressTest() {
        AddressCreateDTO dto = AddressCreateDTO.builder()
                .country("Россия")
                .region("Ростовская область")
                .city("Ростов-на-Дону")
                .street("Большая садовая 100")
                .zipCode(344000)
                .build();

        Address result = addressMapper.toAddress(dto);

        assertThat(result).isNotNull();
        assertThat(result.getCountry()).isEqualTo(dto.getCountry());
        assertThat(result.getRegion()).isEqualTo(dto.getRegion());
        assertThat(result.getCity()).isEqualTo(dto.getCity());
        assertThat(result.getStreet()).isEqualTo(dto.getStreet());
        assertThat(result.getZipCode()).isEqualTo(dto.getZipCode());
    }
}