package com.simats.saferoadschennaisrc;

public class ComplaintModel {
    private String id;
    private String title;
    private String zone;
    private String reporter;
    private String status;
    private String date;
    private String priority;
    private int imageResId;

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId) {
        this.id = id;
        this.title = title;
        this.zone = zone;
        this.reporter = reporter;
        this.status = status;
        this.date = date;
        this.priority = priority;
        this.imageResId = imageResId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getZone() {
        return zone;
    }

    public String getReporter() {
        return reporter;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getPriority() {
        return priority;
    }

    public int getImageResId() {
        return imageResId;
    }
}
