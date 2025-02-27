package ru.kalimulin.dto.addressDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {
    private String country;
    private String region;
    private String city;
    private String street;
    private Integer zipCode;
}