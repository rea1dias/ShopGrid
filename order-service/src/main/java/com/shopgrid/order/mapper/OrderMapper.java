package com.shopgrid.order.mapper;

import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.domain.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    OrderResponse toResponse(Order order);



}
