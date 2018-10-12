package model;

/**
 * Event data class.
 */
public class Event extends Model {

    /** Unique event identifier */
    private String eventID;

    /** User to which this person belongs */
    private String descendant;

    /** ID of person to which this event belongs (personID) */
    private String personID;

    /** Latitude of event's location */
    private double latitude;

    /** Longitude of event's location */
    private double longitude;

    /** Country in which event occurred */
    private String country;

    /** Place in which event occurred*/
    private String city;

    /** Type of event */
    private String eventType;

    /** Year in which event occurred */
    private String year;

    /** All values constructor */
    public Event(String eventID, String username, String personID, double latitude, double longitude,
                 String country, String city, String eventType, String year) {
        this.descendant = username;
        this.eventID = eventID;
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    /** Auto-ID constructor */
    public Event(String username, String personID, double latitude, double longitude,
                 String country, String city, String eventType, String year) {
        this.descendant = username;
        this.eventID = super.generateID();
        this.personID = personID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.city = city;
        this.eventType = eventType;
        this.year = year;
    }

    /** Constructor with place */
    public Event(String username, String personID, Place place, String eventType, String year) {
        this.descendant = username;
        this.eventID = super.generateID();
        this.personID = personID;
        this.latitude = place.getLatitude();
        this.longitude = place.getLongitude();
        this.country = place.getCountry();
        this.city = place.getCity();
        this.eventType = eventType;
        this.year = year;
    }

    // Getters & Setters
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getDescendant() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
