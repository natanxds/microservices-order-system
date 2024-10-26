package com.natanxds.stock.service;

import com.natanxds.stock.exception.InsufficientStockException;
import com.natanxds.stock.exception.ResourceNotFoundException;
import com.natanxds.stock.model.CreateStockRequestDto;
import com.natanxds.stock.model.Stock;
import com.natanxds.stock.model.StockRequestDto;
import com.natanxds.stock.model.StockResponse;
import com.natanxds.stock.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Slf4j
public class StockService {


    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public StockResponse createStock(CreateStockRequestDto stockRequest) {
        log.info("createStock - Creating stock: {}", stockRequest);
        Optional<Stock> existingStock = stockRepository.findStockByProductName(stockRequest.productName());
        if(existingStock.isPresent()){
            Stock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + stockRequest.quantity());
            stockRepository.save(stock);
            return new StockResponse(stock.getProductName(), stock.getQuantity());
        }
        Stock stock = new Stock(stockRequest.productName(), stockRequest.quantity());
        stockRepository.save(stock);
        return new StockResponse(stock.getProductName(), stock.getQuantity());
    }

    public StockResponse getStockByProductName(String productName) {
        log.info("getStock - Fetching stock for product: {}", productName);
        Stock stock = stockRepository.findStockByProductName(productName)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productName));
        return new StockResponse(stock.getProductName(), stock.getQuantity());
    }

    public StockResponse updateStock(Long productId, Stock stockDetails) {
        // TODO: update stock
        log.info("updateStock - Updating stock for product: {}", productId);
        Stock stock = getStockById(productId).toEntity();
        stock.setQuantity(stockDetails.getQuantity());
        stockRepository.save(stock);
        return new StockResponse(stock.getProductName(), stock.getQuantity());
    }

    public StockResponse getStockById(Long productId) {
        return stockRepository.findById(productId)
                .map(stock -> new StockResponse(stock.getProductName(), stock.getQuantity()))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    @Transactional
    private void decreaseStock(Long productId, int quantityOrdered) {
        log.info("decreaseStock - Decreasing stock for product: {}", productId);
        Stock stock = getStockById(productId).toEntity();
        if (stock.getQuantity() < quantityOrdered) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
        stock.setQuantity(stock.getQuantity() - quantityOrdered);
        stockRepository.save(stock);
    }
}
