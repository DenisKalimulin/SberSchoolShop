package ru.kalimulin.dto.walletDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletCreateDTO {

    @Size(min = 4, max = 4, message = "PIN должен содержать ровно 4 символа")
    @Pattern(regexp = "\\d{4}", message = "PIN должен содержать только цифры")
    @NotBlank(message = "Поле с PIN не может быть пустым")
    private String pin;
}