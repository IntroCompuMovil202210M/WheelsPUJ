package Models;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    private boolean completed;
    private List<String> userMail;
    private String name;
    private  String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<String> getUserMail() {
        return userMail;
    }

    public void setUserMail(List<String> userMail) {
        this.userMail = userMail;
    }



    public Trip() {
    }

    public Trip(String name, List<String> user,String date) {
        this.userMail = user;
        this.name = name;
        this.completed = true;
        this.date=date;
    }
}
