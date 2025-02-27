package ru.kalimulin.mappers.walletMapper;

import org.springframework.stereotype.Component;
import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.models.Wallet;

@Component
public class WalletMapperImpl implements WalletMapper {

    @Override
    public WalletResponseDTO toWalletResponseDTO(Wallet wallet) {
        if (wallet == null) {
            return null;
        }

        return WalletResponseDTO.builder()
                .balance(wallet.getBalance())
                .walletNumber(wallet.getWalletNumber())
                .build();
    }

    @Override
    public Wallet toWallet(WalletCreateDTO walletCreateDTO) {
        if (walletCreateDTO == null) {
            return null;
        }

        return Wallet.builder()
                .pin(walletCreateDTO.getPin())
                .build();
    }
}
