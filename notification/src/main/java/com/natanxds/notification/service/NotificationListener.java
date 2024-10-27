package com.natanxds.notification.service;

import com.natanxds.notification.model.Notification;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    @RabbitListener(queues = "notification.queue")
    public void receiveNotification(Notification notification) {
        System.out.println("Received notification: " + notification.getMessage());
    }
}
