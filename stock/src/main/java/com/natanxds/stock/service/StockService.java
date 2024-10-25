package com.natanxds.stock.service;

import com.natanxds.stock.exception.InsufficientStockException;
import com.natanxds.stock.exception.ResourceNotFoundException;
import com.natanxds.stock.model.OrderMessage;
import com.natanxds.stock.model.Stock;
import com.natanxds.stock.repository.StockRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock getStock(String productId) {
        return stockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }

    public Stock updateStock(String productId, Stock stockDetails) {
        // TODO: update stock
        Stock stock = getStock(productId);
        stock.setQuantity(stockDetails.getQuantity());
        return stockRepository.save(stock);
    }

    @Transactional
    public void decreaseStock(String productId, int quantityOrdered) {
        Stock stock = getStock(productId);
        if (stock.getQuantity() < quantityOrdered) {
            throw new InsufficientStockException("Insufficient stock for product: " + productId);
        }
        stock.setQuantity(stock.getQuantity() - quantityOrdered);
        stockRepository.save(stock);
    }

    @RabbitListener(queues = "order.queue")
    public void listenOrder(OrderMessage orderMessage) {
        // TODO: descrease stock only if payment is successful
       // decreaseStock(orderMessage.productId(), orderMessage.quantity());

        isProductInStock(orderMessage.productId(), orderMessage.quantity());
    }

    private boolean isProductInStock(String productId, int quantity) {
        Optional<Stock> stock = stockRepository.findById(productId);
        return stock.isPresent() && stock.get().getQuantity() >= quantity;
    }
}
