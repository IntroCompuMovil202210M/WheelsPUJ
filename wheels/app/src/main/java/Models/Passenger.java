package Models;

public class Passenger extends User {
    private boolean inTrip;

    public Passenger(String name, String surname, String mail, String password, String image) {
        super(name, surname, mail, password, image);
        this.inTrip = false;
    }

    public boolean isInTrip() {
        return inTrip;
    }

    public void setInTrip(boolean inTrip) {
        this.inTrip = inTrip;
    }

}
