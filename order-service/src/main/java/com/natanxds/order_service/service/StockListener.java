package com.natanxds.order_service.service;

import com.natanxds.order_service.model.PaymentStatusUpdate;
import com.natanxds.order_service.model.StockResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockListener {
    private final OrderService orderService;

    public StockListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "stock.queue")
    public void handleStock(StockResponse stockResponse) {
        orderService.isStockAvailable(stockResponse);
    }
}
}
