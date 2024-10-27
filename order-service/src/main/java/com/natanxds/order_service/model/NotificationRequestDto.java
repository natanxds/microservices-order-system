package com.natanxds.order_service.model;

public record NotificationRequestDto(
        String recipient,
        String message
) {
}
