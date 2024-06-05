package com.example.mapapplication;

public class MarkerData {
    public String id;
    public String title;
    public String message;
    public String name;
    public String imageUrl;
    public double latitude;
    public double longitude;

    public MarkerData() {

    }

    public MarkerData(String id, String title, String message, String name, String imageUrl, double latitude, double longitude) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.name = name;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
