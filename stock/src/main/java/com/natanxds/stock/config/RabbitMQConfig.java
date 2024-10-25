package com.natanxds.stock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order.exchange");
    }

    @Bean
    public Queue orderQueue() {
        return new Queue("order.queue");
    }

    @Bean
    public Binding binding(
            @Qualifier("orderQueue") Queue orderQueue, @Qualifier("orderExchange") DirectExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with("order.new");
    }

    @Bean
    public DirectExchange stockExchange() {
        return new DirectExchange("stock.exchange");
    }

    @Bean
    public Queue stockQueue() {
        return new Queue("stock.queue");
    }

    @Bean
    public Binding stockBinding(
            @Qualifier("stockQueue") Queue stockQueue, @Qualifier("stockExchange") DirectExchange stockExchange) {
        return BindingBuilder.bind(stockQueue).to(stockExchange).with("stock.status");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter);
        return template;
    }

    @Bean
    public SimpleMessageListenerContainer orderListenerContainer(ConnectionFactory connectionFactory,
                                                                   Queue orderQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(orderQueue);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            try {
                String messageBody = new String(message.getBody());
                log.info("Message received from the queue: {}", messageBody);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("Error processing message: {}", e.getMessage(), e);
                try {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                } catch (IOException ioException) {
                    log.error("Error sending Nack: {}", ioException.getMessage(), ioException);
                }
            }
        });
        return container;
    }
}
