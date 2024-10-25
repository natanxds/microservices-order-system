package com.natanxds.payment.model;

public record PaymentStatusUpdate(
        String orderId,
        String status
) {}
