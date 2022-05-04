package com.example.wheelspuj.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Route implements Serializable {
    private String id;
    private String idDriver;
    private String idCar;
    private com.example.wheelspuj.models.Geopoint startGeopoint;
    private com.example.wheelspuj.models.Geopoint endGeopoint;
    private String clave;
    private int limit;
    private ArrayList<Passenger> passengers;

    public Route() {
    }

    public Route(String id, String idDriver, com.example.wheelspuj.models.Geopoint startGeopoint, com.example.wheelspuj.models.Geopoint endGeopoint, String clave, int limit, ArrayList<com.example.wheelspuj.models.Passenger> passengers) {
        this.id = id;
        this.idDriver = idDriver;
        this.startGeopoint = startGeopoint;
        this.endGeopoint = endGeopoint;
        this.clave = clave;
        this.limit = limit;
        this.passengers = passengers;

    }

    public String getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(String idDriver) {
        this.idDriver = idDriver;
    }

    public void setPassengers(ArrayList<com.example.wheelspuj.models.Passenger> passengers) {
        this.passengers = passengers;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public com.example.wheelspuj.models.Geopoint getStartPoint() {
        return startGeopoint;
    }

    public void setStartPoint(com.example.wheelspuj.models.Geopoint startGeopoint) {
        this.startGeopoint = startGeopoint;
    }

    public com.example.wheelspuj.models.Geopoint getEndPoint() {
        return endGeopoint;
    }

    public void setEndPoint(com.example.wheelspuj.models.Geopoint endGeopoint) {
        this.endGeopoint = endGeopoint;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ArrayList<com.example.wheelspuj.models.Passenger> getPassengers() {
        return passengers;
    }

    public void addPassenger(com.example.wheelspuj.models.Passenger p) {
        this.passengers.add(p);
    }

    public boolean full() {
        return passengers.size() < limit;
    }
}
