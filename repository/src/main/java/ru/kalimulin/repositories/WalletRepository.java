package ru.kalimulin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kalimulin.models.User;
import ru.kalimulin.models.Wallet;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);

    boolean existsByWalletNumber(String walletNumber);

    Optional<Wallet> findByWalletNumber(String walletNumber);
}
