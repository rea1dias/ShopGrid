package com.shopgrid.order.common.enums;

import java.util.Set;

public enum OrderStatus {

    PENDING {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(RESERVED, CANCELLED);
        }
    },
    RESERVED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(CONFIRMED, PAYMENT_FAILED, CANCELLED);
        }

    },
    PAYMENT_FAILED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(CONFIRMED, CANCELLED);
        }

    },
    CONFIRMED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(SHIPPED);
        }
    },
    SHIPPED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(DELIVERED);
        }
    },
    DELIVERED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(REFUND_REQUESTED);
        }
    },
    REFUND_REQUESTED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(REFUND_APPROVED, REFUND_REJECTED);
        }
    },
    REFUND_APPROVED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of(REFUND_COMPLETED);
        }
    },
    REFUND_REJECTED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of();
        }
    },
    REFUND_COMPLETED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of();
        }
    },
    CANCELLED {
        @Override
        public Set<OrderStatus> allowedTransitions() {
            return Set.of();
        }
    };

    public abstract Set<OrderStatus> allowedTransitions();

    public boolean canTransition(OrderStatus next) {
        return allowedTransitions().contains(next);
    }
}
