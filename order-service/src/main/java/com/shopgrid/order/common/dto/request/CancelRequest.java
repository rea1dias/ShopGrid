package com.shopgrid.order.common.dto.request;

import com.shopgrid.order.common.enums.CancelReason;

public record CancelRequest(
        CancelReason cancelReason
) {
}
