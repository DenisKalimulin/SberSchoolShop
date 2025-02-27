package ru.kalimulin.dto.addressDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressCreateDTO {
    @NotBlank(message = "Необходимо указать страну")
    private String country;

    @NotBlank(message = "Необходимо указать регион")
    private String region;

    @NotBlank(message = "Необходимо указать город")
    private String city;

    @NotBlank(message = "Необходимо указать улицу")
    private String street;

    @NotBlank(message = "Необходимо указать почтовый индекс")
    private Integer zipCode;
}
