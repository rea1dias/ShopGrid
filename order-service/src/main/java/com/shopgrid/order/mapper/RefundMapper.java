package com.shopgrid.order.mapper;

import com.shopgrid.order.common.dto.response.RefundResponse;
import com.shopgrid.order.domain.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    RefundResponse toResponse(Refund refund);
}
