package ru.kalimulin.dto.walletDTO;

import jakarta.validation.constraints.DecimalMin;
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
public class WalletUpdateBalanceDTO {
    @NotNull(message = "Вы должны указать сумму пополнения")
    @DecimalMin(value = "0.01", message = "Минимальная сумма пополнения кошелька - 10 рублей")
    private BigDecimal amount;
}

