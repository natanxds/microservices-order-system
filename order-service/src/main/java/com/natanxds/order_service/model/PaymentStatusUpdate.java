package com.natanxds.order_service.model;

public record PaymentStatusUpdate(
        String orderId,
        String status
) {
}
