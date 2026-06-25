package com.shopgrid.order.mapper;

import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.common.dto.response.OrderStatusHistoryResponse;
import com.shopgrid.order.domain.Order;
import com.shopgrid.order.domain.OrderStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    OrderResponse toResponse(Order order);

    @Mapping(target = "orderId", source = "id")
    OrderStatusHistoryResponse toHistoryResponse(OrderStatusHistory orderStatusHistory);
}
