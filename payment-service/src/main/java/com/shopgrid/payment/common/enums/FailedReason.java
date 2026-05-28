package com.shopgrid.payment.common.enums;

public enum FailedReason {
    INSUFFICIENT_FUNDS("The account does not have enough balance."),
    EXPIRED_CARD("The payment card has expired."),
    INVALID_CVV("The CVV provided is incorrect."),
    CARD_DECLINED("The card issuer declined the transaction without a specific reason."),
    EXPIRED_OTP("The one-time password or authentication code expired."),
    DAILY_LIMIT_EXCEEDED("The transaction exceeds the allowed daily spending limit."),
    INVALID_ACCOUNT("The account details provided are invalid or frozen."),
    TIMEOUT("The payment gateway timed out before processing the transaction."),
    SYSTEM_ERROR("An internal server error occurred while processing the payment.");

    private final String message;

    FailedReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
