package com.example.wheelspuj.models;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomLocation {
    private double latitude;
    private double longitude;
    private String date;

    public CustomLocation() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitude", getLatitude());
            obj.put("longitude", getLongitude());
            obj.put("date", getDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
