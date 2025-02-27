package ru.kalimulin.mappers.walletMapper;

import ru.kalimulin.dto.walletDTO.WalletCreateDTO;
import ru.kalimulin.dto.walletDTO.WalletResponseDTO;
import ru.kalimulin.models.Wallet;

public interface WalletMapper {
    WalletResponseDTO toWalletResponseDTO(Wallet wallet);
    Wallet toWallet (WalletCreateDTO walletCreateDTO);
}
