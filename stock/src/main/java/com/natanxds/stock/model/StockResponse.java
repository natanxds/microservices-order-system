package com.natanxds.stock.model;

public record StockResponse(
        String productName,
        int quantity
) {
    public Stock toEntity() {
        return new Stock(null, productName, quantity);
    }
}
