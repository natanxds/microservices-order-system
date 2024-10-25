package com.natanxds.payment.service;

import com.natanxds.payment.model.PaymentStatusUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentPublisher {

    private final RabbitTemplate rabbitTemplate;


    public PaymentPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void notifyOrderStatus(String orderId, String status){
        log.info("notifyOrderStatus- Notifying order status update: {} - {}", orderId, status);
        PaymentStatusUpdate update = new PaymentStatusUpdate(orderId, status);
        rabbitTemplate.convertAndSend("payment.exchange", "payment.status", update);
        log.info("notifyOrderStatus - Order status update notification sent");
    }
}
