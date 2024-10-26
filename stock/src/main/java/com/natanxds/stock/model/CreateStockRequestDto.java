package com.natanxds.stock.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateStockRequestDto
        (
                @NotBlank(message = "Product name cannot be blank")
                String productName,
                @Min(value = 0, message = "Quantity must be greater or equal to 0")
                int quantity) {
    public Stock toEntity() {
        return new Stock(productName, quantity);
    }
}
