package com.example.wheelspuj.models;

import java.io.Serializable;

public class Car implements Serializable {

    private String id;
    private String id_driver;
    private int limit;

    public Car() {
    }

    public Car(String id, String id_driver, int limit) {
        this.id = id;
        this.id_driver = id_driver;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_driver() {
        return id_driver;
    }

    public void setId_driver(String id_driver) {
        this.id_driver = id_driver;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return id;
    }
}
