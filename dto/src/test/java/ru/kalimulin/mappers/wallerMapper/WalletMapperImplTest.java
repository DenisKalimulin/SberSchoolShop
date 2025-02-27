package ru.kalimulin.mappers.wallerMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.mappers.walletMapper.WalletMapperImpl;
import ru.kalimulin.models.Wallet;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class WalletMapperImplTest {

    private WalletMapperImpl walletMapper;

    @BeforeEach
    void setUp() {
        walletMapper = new WalletMapperImpl();
    }

    @Test
    void toWalletResponseDTO() {
        Wallet wallet = Wallet.builder()
                .balance(new BigDecimal("100.50"))
                .walletNumber("1234567890")
                .build();

        WalletResponseDTO responseDTO = walletMapper.toWalletResponseDTO(wallet);

        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.getBalance()).isEqualByComparingTo("100.50");
        assertThat(responseDTO.getWalletNumber()).isEqualTo("1234567890");
    }

    @Test
    void toWallet() {
        WalletCreateDTO walletCreateDTO = WalletCreateDTO.builder()
                .pin("1234")
                .build();

        Wallet wallet = walletMapper.toWallet(walletCreateDTO);

        assertThat(wallet).isNotNull();
        assertThat(wallet.getPin()).isEqualTo(1234);
    }
}