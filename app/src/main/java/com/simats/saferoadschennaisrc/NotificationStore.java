package com.simats.saferoadschennaisrc;

import java.util.ArrayList;
import java.util.List;

public class NotificationStore {
    private static NotificationStore instance;
    private List<NotificationModel> notifications = new ArrayList<>();

    private NotificationStore() {
        // Mock some initial notifications
        addNotification(new NotificationModel("1", "Welcome", "Welcome to Safe Roads Chennai!", "Just now", "CITIZEN"));
        // Add a mock authority notification for testing unread logic
        addNotification(
                new NotificationModel("2", "New Alert", "Road closure on Anna Salai.", "5 mins ago", "AUTHORITY"));
    }

    public static synchronized NotificationStore getInstance() {
        if (instance == null) {
            instance = new NotificationStore();
        }
        return instance;
    }

    public void addNotification(NotificationModel notification) {
        notifications.add(0, notification); // Newest first
    }

    public List<NotificationModel> getNotifications(String type) {
        List<NotificationModel> filtered = new ArrayList<>();
        for (NotificationModel n : notifications) {
            if (n.getType().equals(type)) {
                filtered.add(n);
            }
        }
        return filtered;
    }

    public void clearNotifications(String type) {
        notifications.removeIf(n -> n.getType().equals(type));
    }

    public void markAllAsRead(String type) {
        for (NotificationModel n : notifications) {
            if (n.getType().equals(type)) {
                n.setRead(true);
            }
        }
    }

    public boolean hasUnreadNotifications(String type) {
        for (NotificationModel n : notifications) {
            if (n.getType().equals(type) && !n.isRead()) {
                return true;
            }
        }
        return false;
    }
}
