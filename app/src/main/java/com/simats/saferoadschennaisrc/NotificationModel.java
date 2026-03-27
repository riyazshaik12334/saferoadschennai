package com.simats.saferoadschennaisrc;

import com.google.gson.annotations.SerializedName;

public class NotificationModel {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("message")
    private String message;
    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("is_read")
    private boolean isRead;

    @SerializedName("type")
    private String type; // "CITIZEN" or "AUTHORITY"

    @SerializedName("user_email")
    private String userEmail;

    public NotificationModel(String id, String title, String message, String timestamp, String type) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getType() {
        return type;
    }
}
