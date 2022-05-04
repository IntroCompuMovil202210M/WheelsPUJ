package com.example.wheelspuj.services;

import androidx.annotation.NonNull;

import com.example.wheelspuj.models.Car;
import com.example.wheelspuj.models.Driver;
import com.example.wheelspuj.models.Passenger;
import com.example.wheelspuj.models.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseService {

    private DatabaseReference mDatabase;

    private List<Driver> drivers;
    private Driver currentDriver;

    private List<Route> routes;
    private Route currentRoute;

    private List<Car> cars;
    private Car currentCar;

    private List<Passenger> passengers;
    private Passenger currentPassenger;

    public FirebaseService() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void writeNewDriver(Driver driver) {
        mDatabase.child("drivers").child(driver.getId()).setValue(driver);
    }

    public List<Driver> getDrivers() {
        drivers = new ArrayList<>();
        mDatabase.child("drivers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, Driver>> type = new GenericTypeIndicator<HashMap<String, Driver>>() {
                    };
                    HashMap<String, Driver> driversResult = task.getResult().getValue(type);
                    drivers.addAll(driversResult.values());
                }
            }
        });

        return drivers;
    }

    public Driver getDriver(String id) {
        currentDriver = new Driver();
        mDatabase.child("drivers").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currentDriver = task.getResult().getValue(Driver.class);
                }
            }
        });

        return currentDriver;
    }

    public void writeNewRoute(Route route) {
        mDatabase.child("routes").child(route.getId()).setValue(route);
    }

    public List<Route> getRoutes() {
        routes = new ArrayList<>();
        mDatabase.child("drivers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, Route>> type = new GenericTypeIndicator<HashMap<String, Route>>() {
                    };
                    HashMap<String, Route> routesResult = task.getResult().getValue(type);
                    routes.addAll(routesResult.values());
                }
            }
        });

        return routes;
    }

    public Route getRoute(String id) {
        currentRoute = new Route();
        mDatabase.child("routes").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currentRoute = task.getResult().getValue(Route.class);
                }
            }
        });

        return currentRoute;
    }

    public void writeNewCar(Car car) {
        mDatabase.child("cars").child(car.getId()).setValue(car);
    }

    public List<Car> getCars() {
        cars = new ArrayList<>();
        mDatabase.child("cars").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, Car>> type = new GenericTypeIndicator<HashMap<String, Car>>() {
                    };
                    HashMap<String, Car> carsResult = task.getResult().getValue(type);
                    cars.addAll(carsResult.values());
                }
            }
        });

        return cars;
    }

    public Car getCar(String id) {
        currentCar = new Car();
        mDatabase.child("cars").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currentCar = task.getResult().getValue(Car.class);
                }
            }
        });

        return currentCar;
    }

    public void writeNewPassenger(Passenger passenger) {
        mDatabase.child("passengers").child(passenger.getId()).setValue(passenger);
    }

    public List<Passenger> getPassengers() {
        passengers = new ArrayList<>();
        mDatabase.child("passengers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    GenericTypeIndicator<HashMap<String, Passenger>> type = new GenericTypeIndicator<HashMap<String, Passenger>>() {
                    };
                    HashMap<String, Passenger> passengersResult = task.getResult().getValue(type);
                    passengers.addAll(passengersResult.values());
                }
            }
        });

        return passengers;
    }

    public Passenger getPassenger(String id) {
        currentPassenger = new Passenger();
        mDatabase.child("passengers").child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    currentPassenger = task.getResult().getValue(Passenger.class);
                }
            }
        });

        return currentPassenger;
    }

}
