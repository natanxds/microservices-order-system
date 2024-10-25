package com.natanxds.payment.service;

import com.natanxds.payment.model.OrderRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentConsumerService {

    private final PaymentPublisher paymentPublisher;

    public PaymentConsumerService(PaymentPublisher paymentPublisher) {
        this.paymentPublisher = paymentPublisher;
    }

    @RabbitListener(queues = "order.queue")
    public void processOrder(OrderRequest orderRequest){
        log.info("processOrder - Processing order: {}", orderRequest);
        paymentPublisher.notifyOrderStatus(orderRequest.orderId(), "PAYMENT_PENDING");
        log.info("processOrder - Order processed");
    }

}
