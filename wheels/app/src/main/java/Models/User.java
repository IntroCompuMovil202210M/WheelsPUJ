package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Random;

public class User implements Serializable, Parcelable {
    protected String name;
    protected String surname;
    protected String mail;
    protected String password;
    protected String image;
    protected int id;
    protected Position position;

    protected User(Parcel in) {
        name = in.readString();
        surname = in.readString();
        mail = in.readString();
        password = in.readString();
        image = in.readString();
        id = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Position getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Position startPoint) {
        this.startPoint = startPoint;
    }

    public Position getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Position endPoint) {
        this.endPoint = endPoint;
    }

    protected Position startPoint;
    protected Position endPoint;

    public User() {
    }


    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                ", id=" + id +
                ", position=" + position +
                '}';
    }

    public User(String name, String surname, String mail, String password, String image) {
        this.id = new Random().nextInt(500);
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
        this.image = image;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(name);
        parcel.writeString(surname);
        parcel.writeString(mail);
        parcel.writeString(password);
        parcel.writeString(image);
        parcel.writeInt(id);
    }


}
