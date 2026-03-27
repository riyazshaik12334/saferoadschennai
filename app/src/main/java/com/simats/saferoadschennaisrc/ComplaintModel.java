package com.simats.saferoadschennaisrc;

import com.google.gson.annotations.SerializedName;

public class ComplaintModel {
    @SerializedName("report_id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("zone")
    private String zone;
    @SerializedName("reporter")
    private String reporter;
    @SerializedName("status")
    private String status;
    @SerializedName("date")
    private String date;
    @SerializedName("priority")
    private String priority;
    @SerializedName("supervisor_name")
    private String supervisor_name;
    @SerializedName("supervisor_image")
    private String supervisor_image;
    @SerializedName("proof")
    private String proof;
    private int supervisor_image_res_id;
    @SerializedName("image_res_id")
    private int imageResId;
    @SerializedName("image")
    private String imageUri;
    @SerializedName("description")
    private String description;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("reporter_mobile")
    private String reporterMobile;
    @SerializedName("reporter_email")
    private String reporterEmail;
    @SerializedName("supervisor_updated_at")
    private String supervisorUpdatedAt;
    @SerializedName("authority_name")
    private String authorityName;
    @SerializedName("rating")
    private Integer rating;
    @SerializedName("rating_description")
    private String ratingDescription;

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId, String imageUri, String description, double latitude, double longitude,
            String reporterMobile, String reporterEmail) {
        this.id = id;
        this.title = title;
        this.zone = zone;
        this.reporter = reporter;
        this.status = status;
        this.date = date;
        this.priority = priority;
        this.imageResId = imageResId;
        this.imageUri = imageUri;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reporterMobile = reporterMobile;
        this.reporterEmail = reporterEmail;
    }

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId, String imageUri, String description, double latitude, double longitude,
            String reporterMobile) {
        this(id, title, zone, reporter, status, date, priority, imageResId, imageUri, description, latitude, longitude,
                reporterMobile, "");
    }

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId, String imageUri, String description, double latitude, double longitude) {
        this(id, title, zone, reporter, status, date, priority, imageResId, imageUri, description, latitude, longitude,
                "", "");
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId, String imageUri, String description) {
        this(id, title, zone, reporter, status, date, priority, imageResId, imageUri, description, 0, 0);
    }

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId, String imageUri) {
        this(id, title, zone, reporter, status, date, priority, imageResId, imageUri, "");
    }

    public ComplaintModel(String id, String title, String zone, String reporter, String status, String date,
            String priority, int imageResId) {
        this(id, title, zone, reporter, status, date, priority, imageResId, null);
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

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getReporterMobile() {
        return reporterMobile;
    }

    public String getSupervisorName() {
        return supervisor_name;
    }

    public void setSupervisorName(String supervisor_name) {
        this.supervisor_name = supervisor_name;
    }

    public String getSupervisorImage() {
        return supervisor_image;
    }

    public void setSupervisorImage(String supervisor_image) {
        this.supervisor_image = supervisor_image;
    }

    public int getSupervisorImageResId() {
        return supervisor_image_res_id;
    }

    public void setSupervisorImageResId(int supervisor_image_res_id) {
        this.supervisor_image_res_id = supervisor_image_res_id;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getSupervisorUpdatedAt() {
        return supervisorUpdatedAt;
    }

    public void setSupervisorUpdatedAt(String supervisorUpdatedAt) {
        this.supervisorUpdatedAt = supervisorUpdatedAt;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getRatingDescription() {
        return ratingDescription;
    }

    public void setRatingDescription(String ratingDescription) {
        this.ratingDescription = ratingDescription;
    }
}
