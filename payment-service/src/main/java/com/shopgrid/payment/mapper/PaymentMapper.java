package com.shopgrid.payment.mapper;

import com.shopgrid.payment.domain.dto.response.PaymentResponse;
import com.shopgrid.payment.domain.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentResponse toResponse(Payment payment);
}
