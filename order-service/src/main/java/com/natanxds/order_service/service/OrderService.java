package com.natanxds.order_service.service;

import com.natanxds.order_service.exceptions.OrderAlreadyExistsException;
import com.natanxds.order_service.model.OrderRequest;
import com.natanxds.order_service.model.StockResponse;
import com.natanxds.order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.natanxds.order_service.model.Order;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Order createOrder(OrderRequest orderRequest) {
        log.info("createOrder - Creating order: {}", orderRequest);
        if(isStockAvailable(orderRequest)){
            return sendOrder(orderRequest);
        }
        if(orderRepository.findByOrderId(orderRequest.orderId()).isPresent()){
            throw new OrderAlreadyExistsException("Order already exists");
        }
        rabbitTemplate.convertAndSend("order.exchange", "order.new", orderRequest);
        return orderRepository.save(this.orderToEntity(orderRequest));
    }

    public List<Order> getAllOrders() {
        log.info("getAllOrders - Fetching all orders");
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        log.info("getOrderById - Fetching order by id: {}", id);
        return orderRepository.findById(id);
    }

    public void updateOrderStatus(String orderId, String status) {
        log.info("updateOrderStatus - Updating order status for order id: {} to status: {}", orderId, status);
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
        log.info("updateOrderStatus - Order status updated");
    }

    public Order orderToEntity(OrderRequest orderRequest){
        return new Order(orderRequest.orderId(), orderRequest.product(), orderRequest.quantity());
    }

}
