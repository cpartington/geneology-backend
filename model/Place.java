package model;

public class Place {

    String country;

    String city;

    double latitude;

    double longitude;

    /** Default constructor */
    public Place() {

    }

    /** Parameterized constructor */
    public Place(String country, String city, double latitude, double longitude) {
        this.country = country;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters & Setters
    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
