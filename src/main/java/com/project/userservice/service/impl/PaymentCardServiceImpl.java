package com.project.userservice.service.impl;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
import com.project.userservice.dto.response.UserResponse;
import com.project.userservice.entity.PaymentCard;
import com.project.userservice.entity.User;
import com.project.userservice.entity.enums.PaymentCardStatus;
import com.project.userservice.exception.limit.CardLimitExceededException;
import com.project.userservice.exception.notfound.PaymentCardNotFoundException;
import com.project.userservice.exception.notfound.UserNotFoundException;
import com.project.userservice.repository.PaymentCardRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.service.PaymentCardService;
import com.project.userservice.service.mapper.PaymentCardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @CachePut(value = "cards", key = "#result.id")
    public PaymentCardResponse addCardToUser(UUID userId, PaymentCardRequest dto) {
        long count = paymentCardRepository.countByUserId(userId);

        if(count > 5) {
            throw new CardLimitExceededException(userId);
        }

        var user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        var card = paymentCardMapper.toEntity(dto);
        user.addPaymentCard(card);

        var savedCard = paymentCardRepository.save(card);

        Optional.ofNullable(cacheManager.getCache("users")).ifPresent(
                cache -> {
                    cache.evict(userId);
                    cache.evict(user.getEmail());
                });

        return paymentCardMapper.toDto(savedCard);
    }

    @Override
    @Cacheable(value = "cards", key = "#id")
    public PaymentCardResponse getCardById(UUID id) {
        return paymentCardRepository.findById(id)
                .map(paymentCardMapper::toDto)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
    }

    @Override
    public List<PaymentCardResponse> getAllCardsByUserId(UUID userId) {
        if(!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return paymentCardRepository.findAllByUserId(userId)
                .stream()
                .map(paymentCardMapper::toDto)
                .toList();
    }

    @Override
    public Page<PaymentCardResponse> getAllCards(Pageable pageable) {
        return paymentCardRepository.findAll(pageable)
                .map(paymentCardMapper::toDto);
    }

    @Override
    @Transactional
    @CachePut(value = "cards", key = "#id")
    public PaymentCardResponse updateCardActiveStatus(UUID id, boolean active) {
        int updatedCard = paymentCardRepository.updateCardActiveStatus(id, active);

        if (updatedCard == 0) {
            throw new PaymentCardNotFoundException(id);
        }

        var card = findCardByIdOrThrow(id);

        Optional.ofNullable(cacheManager.getCache("users")).ifPresent(
                cache -> {
                    cache.evict(card.getUser().getId());
                    cache.evict(card.getUser().getEmail());
                });

        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional
    @CacheEvict(value = "cards", key = "#cardId")
    public void deleteCard(UUID userId, UUID cardId) {
        var card = findCardByIdOrThrow(cardId);

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.removePaymentCard(card);
        paymentCardRepository.delete(card);

        Optional.ofNullable(cacheManager.getCache("users")).ifPresent(
                cache -> {
                    cache.evict(userId);
                    cache.evict(user.getEmail());
                });
    }

    private PaymentCard findCardByIdOrThrow(UUID id) {
        return paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
    }
}
