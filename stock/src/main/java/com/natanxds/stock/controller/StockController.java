package com.natanxds.stock.controller;

import com.natanxds.stock.model.CreateStockRequestDto;
import com.natanxds.stock.model.Stock;
import com.natanxds.stock.model.StockRequestDto;
import com.natanxds.stock.model.StockResponse;
import com.natanxds.stock.service.StockService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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
    public ResponseEntity<StockResponse> createStock(@Valid @RequestBody CreateStockRequestDto stock) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.createStock(stock));
    }

    @GetMapping
    public ResponseEntity<StockResponse> getStockByProductName(@PathParam("productName") String productName) {
        return ResponseEntity.ok(stockService.getStockByProductName(productName));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStockById(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<StockResponse> updateStock(@PathVariable Long productId, @Valid @RequestBody Stock stockDetails) {
        return ResponseEntity.ok(stockService.updateStock(productId, stockDetails));
    }
}
