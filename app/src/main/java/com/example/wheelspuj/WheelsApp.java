package com.example.wheelspuj;

import android.app.Application;

import com.parse.Parse;

public class WheelsApp extends Application {
    public static final String IP="10.0.2.2";
    public static final String IP_GCP = "http://"+IP+":1337/parse";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("wheelspuj").clientKey("") // should correspond to Application Id env variable
                .server(IP_GCP)
                .build());
    }
}
