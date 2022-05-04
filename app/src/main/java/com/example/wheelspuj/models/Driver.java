package com.example.wheelspuj.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Driver implements Serializable {

    private String name;
    private String surname;
    private String id;
    private String image;
    private String phone;
    private String email;

    private ArrayList<Route> routes;
    private ArrayList<Car> cars;


    public Driver(String name, String surname, String id, String phone, String email, ArrayList<Route> routes, ArrayList<Car> cars) {
        this.name = name;
        this.surname = surname;
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.routes = routes;
        this.cars = cars;
    }

    public Driver() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }
}
