package com.example.wheelspuj.models;

import java.io.Serializable;

public class Passenger implements Serializable {
    private String id;
    private String name;
    private String surname;
    private String currentRouteId;


    public Passenger(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Passenger() {
    }

    public String getCurrentRouteId() {
        return currentRouteId;
    }

    public void setCurrentRouteId(String currentRouteId) {
        this.currentRouteId = currentRouteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
