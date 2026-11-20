package com.project.userservice.security;

import com.project.userservice.config.SecurityUtils;
import com.project.userservice.repository.PaymentCardRepository;
import com.project.userservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@RequiredArgsConstructor
@Component("securityService")
public class SecurityService {

    private final PaymentCardRepository cardRepository;

    public boolean isOwner(UUID id) {
        if (id == null) {
            return false;
        }
        String currentUserId = SecurityUtils.getCurrentUserId();
        return id.toString().equals(currentUserId);
    }

    public boolean isCardOwner(UUID cardId) {
        if (cardId == null) {
            return false;
        }
        UUID currentUserId = UUID.fromString(SecurityUtils.getCurrentUserId());
        try {
            return cardRepository.existsByIdAndUser_Id(cardId, currentUserId);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}