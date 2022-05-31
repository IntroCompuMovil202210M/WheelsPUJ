package Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    private List<Position> points;
    private Position endPoint;
    private boolean available;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Position endPoint) {
        this.endPoint = endPoint;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Position> getPoints() {
        return points;
    }

    public void setPoints(List<Position> points) {
        this.points = points;
    }

    public Route() {
    }

    public Route(Position endPoint, String name) {
        this.endPoint = endPoint;
        this.available = false;
        this.name = name;


    }

    @Override
    public String toString() {
        return "Route{" +
                "endPoint=" + endPoint +
                ", available=" + available +
                ", name='" + name + '\'' +
                '}';
    }
}
