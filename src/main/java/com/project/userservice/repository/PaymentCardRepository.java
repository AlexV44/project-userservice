package com.project.userservice.repository;

import com.project.userservice.entity.PaymentCard;
import com.project.userservice.entity.enums.PaymentCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, UUID> {
    List<PaymentCard> findAllPaymentCardsByUserId(UUID userId);

    long countByUserId(UUID userId);

    @Modifying
    @Query(value = "UPDATE payment_cards SET active = :active WHERE id = :id", nativeQuery = true)
    int updateCardActiveStatus(@Param("id") UUID id, @Param("active") boolean active);
}
