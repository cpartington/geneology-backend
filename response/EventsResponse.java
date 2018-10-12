package response;

import java.util.ArrayList;

import model.Event;

public class EventsResponse extends Response {

    /** Array of event objects */
    private ArrayList<Event> events;

    /** Default constructor */
    public EventsResponse() {

    }

    /** Parameterized constructor */
    public EventsResponse(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }
}
