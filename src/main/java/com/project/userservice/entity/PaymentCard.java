package com.project.userservice.entity;

import com.project.userservice.entity.enums.PaymentCardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Payment card gateway token is required")
    @Column(name = "gateway_token", nullable = false, unique = true)
    private String gatewayToken;

    @NotBlank(message = "Last 4 digits are required")
    @Size(min = 4, max = 4, message = "Last 4 digits must be exactly 4 numbers")
    @Column(name = "last_4", nullable = false, length = 4)
    private String last4;

    @NotBlank(message = "Cardholder name is required")
    @Column(name = "holder", nullable = false)
    private String holder;

    @Future
    @NotNull(message = "Payment card must have an expiration date")
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment card must have a status")
    @Column(name = "status", nullable = false)
    private PaymentCardStatus status = PaymentCardStatus.PENDING;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
