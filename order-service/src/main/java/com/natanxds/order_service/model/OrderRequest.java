package com.natanxds.order_service.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
        @NotBlank(message = "Order ID cannot be blank") String orderId,
        @NotBlank(message = "Product name is required") String product,
        @Min(value = 1, message = "Quantity must be at least 1") int quantity
) {
}
