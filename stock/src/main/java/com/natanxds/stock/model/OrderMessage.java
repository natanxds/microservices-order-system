package com.natanxds.stock.model;

public record OrderMessage(
        String productId,
        int quantity
) {
}
