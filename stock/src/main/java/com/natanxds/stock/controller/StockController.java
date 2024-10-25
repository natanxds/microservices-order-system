package com.natanxds.stock.controller;

import com.natanxds.stock.model.Stock;
import com.natanxds.stock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@Valid @RequestBody Stock stock) {
        Stock createdStock = stockService.createStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Stock> getStock(@PathVariable String productId) {
        Stock stock = stockService.getStock(productId);
        return ResponseEntity.ok(stock);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Stock> updateStock(@PathVariable String productId, @Valid @RequestBody Stock stockDetails) {
        Stock updatedStock = stockService.updateStock(productId, stockDetails);
        return ResponseEntity.ok(updatedStock);
    }
}
