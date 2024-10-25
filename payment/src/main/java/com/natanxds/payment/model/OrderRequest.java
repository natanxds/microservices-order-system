package com.natanxds.payment.model;

public record OrderRequest(
        String orderId,
        String product,
        int quantity
) {
}
