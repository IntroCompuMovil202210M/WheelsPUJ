package com.example.wheelspuj.models;

import java.io.Serializable;

public class Car implements Serializable {

    private String id;
    private String idDriver;
    private int limit;

    public Car() {
    }

    public Car(String id, String idDriver, int limit) {
        this.id = id;
        this.idDriver = idDriver;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
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
