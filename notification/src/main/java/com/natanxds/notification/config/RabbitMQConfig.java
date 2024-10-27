package com.natanxds.notification.config;

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

@Slf4j
@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange("notifcation.exchange");
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
    public SimpleMessageListenerContainer notificationListenerContainer(ConnectionFactory connectionFactory,
                                                                        Queue notificationQueue) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(notificationQueue);
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
