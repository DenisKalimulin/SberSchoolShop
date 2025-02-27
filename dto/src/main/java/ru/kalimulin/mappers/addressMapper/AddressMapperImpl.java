package ru.kalimulin.mappers.addressMapper;

import org.springframework.stereotype.Component;
import ru.kalimulin.dto.addressDTO.AddressCreateDTO;
import ru.kalimulin.dto.addressDTO.AddressResponseDTO;
import ru.kalimulin.models.Address;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressResponseDTO toAddressResponseDTO(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDTO.builder()
                .country(address.getCountry())
                .region(address.getRegion())
                .city(address.getCity())
                .street(address.getStreet())
                .zipCode(address.getZipCode())
                .build();
    }

    @Override
    public Address toAddress(AddressCreateDTO addressCreateDTO) {
        if (addressCreateDTO == null) {
            return null;
        }

        return Address.builder()
                .country(addressCreateDTO.getCountry())
                .region(addressCreateDTO.getRegion())
                .city(addressCreateDTO.getCity())
                .street(addressCreateDTO.getStreet())
                .zipCode(addressCreateDTO.getZipCode())
                .build();
    }

    @Override
    public List<AddressResponseDTO> toAddressResponseDTOList(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return List.of();
        }

        return addresses.stream()
                .map(this::toAddressResponseDTO) // Преобразуем каждый адрес в DTO
                .collect(Collectors.toList()); // Собираем в List
    }
}