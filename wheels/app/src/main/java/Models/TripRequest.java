package Models;
import java.util.Random;

public class TripRequest {
    private String route;
    private int accepted;
    private Position pickPoint;
    private String mailPassenger;
    private String mailDriver;
    private int id;

    private TripRequest() {
    }


    public TripRequest(String route, String mailPassenger, String mailDriver, Position pickPoint) {
        this.route = route;
        this.pickPoint = pickPoint;
        this.accepted = 0;
        this.id=new Random().nextInt(500);
        this.mailPassenger = mailPassenger;
        this.mailDriver = mailDriver;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public Position getPickPoint() {
        return pickPoint;
    }

    public void setPickPoint(Position pickPoint) {
        this.pickPoint = pickPoint;
    }

    public String getMailPassenger() {
        return mailPassenger;
    }

    public void setMailPassenger(String mailPassenger) {
        this.mailPassenger = mailPassenger;
    }

    public String getMailDriver() {
        return mailDriver;
    }

    public void setMailDriver(String mailDriver) {
        this.mailDriver = mailDriver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
