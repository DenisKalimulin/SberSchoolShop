package ru.kalimulin.mappers.addressMapper;

import ru.kalimulin.dto.addressDTO.AddressCreateDTO;
import ru.kalimulin.dto.addressDTO.AddressResponseDTO;
import ru.kalimulin.models.Address;

import java.util.List;

public interface AddressMapper {
    AddressResponseDTO toAddressResponseDTO(Address address);

    Address toAddress(AddressCreateDTO addressCreateDTO);

    List<AddressResponseDTO> toAddressResponseDTOList(List<Address> addresses);
}
