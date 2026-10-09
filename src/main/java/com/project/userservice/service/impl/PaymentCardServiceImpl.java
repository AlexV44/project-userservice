package com.project.userservice.service.impl;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional
    public PaymentCardResponse addCardToUser(UUID userId, PaymentCardRequest dto) {
        long count = paymentCardRepository.countByUserId(userId);

        if(count > 5) {
            throw new CardLimitExceededException(userId);
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));

        PaymentCard card = paymentCardMapper.toEntity(dto);
        user.addPaymentCard(card);

        PaymentCard savedCard = paymentCardRepository.save(card);

        return paymentCardMapper.toDto(savedCard);
    }

    @Override
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
    public PaymentCardResponse updateCardStatus(UUID id, PaymentCardStatus status) {
       int updated = paymentCardRepository.updateCardStatus(id, status);

       if(updated == 0) {
           throw new PaymentCardNotFoundException(id);
       }

       PaymentCard card = findCardByIdOrThrow(id);

       return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional
    public void deleteCard(UUID userId, UUID cardId) {
        PaymentCard card = findCardByIdOrThrow(cardId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.removePaymentCard(card);
    }

    private PaymentCard findCardByIdOrThrow(UUID id) {
        return paymentCardRepository.findById(id)
                .orElseThrow(() -> new PaymentCardNotFoundException(id));
    }
}
