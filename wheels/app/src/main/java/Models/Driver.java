package Models;

import java.util.ArrayList;
import java.util.List;

public class Driver extends User {

    private List<Route> routes;
    private boolean available;
    private String licensePlate;
    private String carUrl;

    private Driver(){
        super();

    }
    @Override
    public String toString() {
        return "Driver{" +
                "available=" + available +
                ", licensePlate='" + licensePlate + '\'' +
                ", carUrl='" + carUrl + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                ", position=" + position +
                '}';
    }

    public Driver(String name, String surname, String mail, String password, String image, String licensePlate, String carUrl) {
        super(name, surname, mail, password, image);
        this.carUrl = carUrl;
        this.licensePlate = licensePlate;
        this.routes=new ArrayList<>();
        this.available=true;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getCarUrl() {
        return carUrl;
    }

    public void setCarUrl(String carUrl) {
        this.carUrl = carUrl;
    }
}
