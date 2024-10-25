package com.natanxds.order_service.service;

import com.natanxds.order_service.model.Order;
import com.natanxds.order_service.model.PaymentStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentStatusListener {

    private final OrderService orderService;

    public PaymentStatusListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @RabbitListener(queues = "payment.status.queue")
    public void handleOrderStatusUpdate(PaymentStatusUpdate paymentStatusUpdate) {
        log.info("handleOrderStatusUpdate - Received payment status update: {}", paymentStatusUpdate);
        orderService.updateOrderStatus(paymentStatusUpdate.orderId(), paymentStatusUpdate.status());
        log.info("handleOrderStatusUpdate - Order status updated");
    }
}
