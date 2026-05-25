package com.shopgrid.order.common.dto.request;

import java.util.List;

public record OrderRequest(

        List<OrderItemRequest> items
) {}
