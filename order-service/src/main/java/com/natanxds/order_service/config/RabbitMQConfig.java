package com.natanxds.order_service.config;

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

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange("notification.exchange");
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue("notification.queue");
    }

    @Bean
    public Binding notificationBinding(
            @Qualifier("notificationQueue") Queue notificationQueue, @Qualifier("notificationExchange") DirectExchange noficationExchange) {
        return BindingBuilder.bind(notificationQueue).to(noficationExchange).with("notification.new");
    }

    @Bean
    public SimpleMessageListenerContainer paymentListenerContainer(ConnectionFactory connectionFactory,
                                                                        Queue paymentStatusQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(paymentStatusQueue);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // Set acknowledgment mode to MANUAL
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            try {
                log.info("Message received from the queue: {}", new String(message.getBody()));
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.error("Error processing message", e);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        });
        container.setPrefetchCount(1);
        return container;
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
}
