package com.natanxds.order_service.service;

import com.natanxds.order_service.model.StockRequestDto;
import com.natanxds.order_service.model.StockResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StockServiceClient {

    private final RestTemplate restTemplate;

    private final String STOCK_SERVICE_URL = "http://localhost:8083/api/v1/stocks";

    public StockServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public StockResponseDto checkStock(String productName) {
        String url = STOCK_SERVICE_URL + "?productName={productName}";
        return restTemplate.getForObject(url, StockResponseDto.class, productName);
    }
}
