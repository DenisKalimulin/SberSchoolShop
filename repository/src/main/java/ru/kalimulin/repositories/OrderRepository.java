package ru.kalimulin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kalimulin.models.Order;
import ru.kalimulin.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<List<Order>> findByUser(User user);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.items i WHERE o.user = :buyer AND i.product.owner = :seller AND o.status = 'PAID'")
    boolean existsByUserAndSeller(@Param("buyer") User buyer, @Param("seller") User seller);
}