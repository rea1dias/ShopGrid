package com.shopgrid.notification.service;

import com.shopgrid.notification.event.NotificationEvent;

public interface NotificationService {

    void sendNotification(NotificationEvent event);
}
