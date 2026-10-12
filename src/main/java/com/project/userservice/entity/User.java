package com.project.userservice.entity;

import com.project.userservice.entity.audit.BaseAuditEntity;
import com.project.userservice.entity.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Provide valid name")
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @NotBlank(message = "Provide valid surname")
    @Column(name = "surname", nullable = false, length = 40)
    private String surname;

    @NotBlank
    @Email(message = "Provide a valid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Builder.Default
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING;

    @Past
    @NotNull
    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    //@Size()
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PaymentCard> paymentCards = new ArrayList<>();

    public void addPaymentCard(PaymentCard card) {
        paymentCards.add(card);
        card.setUser(this);
    }

    public void removePaymentCard(PaymentCard card) {
        paymentCards.remove(card);
        card.setUser(null);
    }
}
