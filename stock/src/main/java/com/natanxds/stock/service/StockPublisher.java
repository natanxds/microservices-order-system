package com.natanxds.stock.service;

import com.natanxds.stock.model.OrderMessage;
import com.natanxds.stock.model.StockMessage;
import com.natanxds.stock.model.StockResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StockPublisher {
    private final RabbitTemplate rabbitTemplate;


    public StockPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void isProductInStock(String status){
        StockResponse stockResponse = new StockResponse(status);
        rabbitTemplate.convertAndSend("stock.exchange", "stock.instock.", stockResponse);
    }
}
