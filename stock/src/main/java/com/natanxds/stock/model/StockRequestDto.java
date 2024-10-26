package com.natanxds.stock.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StockRequestDto(
        @NotBlank(message = "Product Name cannot be blank") String productName
) {
}
