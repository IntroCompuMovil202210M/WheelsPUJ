package Models;

import java.util.ArrayList;
import java.util.List;

public class Trip {
    private boolean completed;
    private List<String> userMail;
    private String name;
    private Position endPoint;
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



    public Position getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Position endPoint) {
        this.endPoint = endPoint;
    }



    public Trip() {
    }

    public Trip(String name, Position endPoint) {
        this.userMail = new ArrayList<>();
        this.name=name;
        this.completed = false;
        this.endPoint = endPoint;
    }
}
