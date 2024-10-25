package com.natanxds.payment.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Listening to order exchange - order.new
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

    // Publishing to payment exchange - payment.status
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange("payment.exchange");
    }

    @Bean
    public Queue paymentStatusQueue() {
        return new Queue("payment.status.queue");
    }

    @Bean
    public Binding paymentStatusBinding(
            @Qualifier("paymentStatusQueue") Queue paymentStatusQueue, @Qualifier("paymentExchange") DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentStatusQueue).to(paymentExchange).with("payment.status");
    }

    // Other configs

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,
                                                                   Queue orderQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(orderQueue);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            System.out.println("Order received: " + new String(message.getBody()));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        });
        return container;
    }


}
