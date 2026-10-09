package com.project.userservice.service.mapper;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
import com.project.userservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentCardMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentCard toEntity(PaymentCardRequest dto);

    List<PaymentCardResponse> toDtoList(List<PaymentCard> entities);

    PaymentCardResponse toDto(PaymentCard entity);
}
