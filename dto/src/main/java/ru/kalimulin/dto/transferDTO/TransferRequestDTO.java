package ru.kalimulin.dto.transferDTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO {
    @NotBlank(message = "Укажите номер кошелька")
    private String walletNumber;

    @NotNull(message = "Укажите сумму перевода")
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше 0")
    private BigDecimal amount;

    @NotBlank(message = "Укажите PIN-код")
    private String pin;
}
