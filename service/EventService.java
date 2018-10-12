package service;

import java.util.ArrayList;
import java.util.logging.Level;

import dao.AuthTokenDao;
import dao.Dao.*;
import dao.EventDao;
import response.EventResponse;
import response.EventsResponse;
import response.Response;
import model.Event;

/**
 * Service to obtain Event data.
 */
public class EventService extends Service {

    /**
     * Returns the single Event object associated with the specified ID.
     *
     * @param eventID the Event ID
     * @return a Response object containing the Event object associated with the eventID
     */
    public Response event(String eventID, String authToken) {
        AuthTokenDao authTokenDao = new AuthTokenDao();
        EventDao eventDao = null;
        Event event;
        String username = null;

        logger.entering("EventService", "event");

        try {
            authTokenDao.openConnection();
            try {

                // Check that the authToken is valid
                try {
                    username = authTokenDao.getUsername(authToken);
                }
                catch (DoesNotExistException e) {
                    authTokenDao.closeConnection(false);
                    return new Response("Invalid auth token.", "user");
                }

                // Get the event
                try {
                    eventDao = new EventDao(authTokenDao.getConnection());
                    event = eventDao.get(eventID);
                }
                catch (DoesNotExistException e) {
                    eventDao.closeConnection(false);
                    return new Response("Invalid eventID parameter.", "user");
                }

                // Make sure authToken and user match up
                if (!event.getDescendant().equals(username)) {
                    eventDao.closeConnection(false);
                    return new Response("Requested event does not belong to this user.",
                                             "user");
                }
            }
            catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                eventDao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Return response if successful
            eventDao.closeConnection(true);
            return new Response(new EventResponse(event));
        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
        finally {
            logger.exiting("EventService", "event");
        }
    }

    /**
     * Returns all Events for all family members of the current user
     *
     * @param authToken the method to identify the user
     * @return a Response object containing all Events for all family members
     */
    public Response events(String authToken) {
        AuthTokenDao authTokenDao = new AuthTokenDao();
        EventDao eventDao = null;
        ArrayList<Event> events;
        String username = null;

        logger.entering("EventService", "events");

        try {
            authTokenDao.openConnection();
            try {
                // Get the username associated with the auth token
                try {
                    username = authTokenDao.getUsername(authToken);
                } catch (DoesNotExistException e) {
                    authTokenDao.closeConnection(false);
                    return new Response("Invalid auth token.", "user");
                }

                // Get events associated with the user
                eventDao = new EventDao(authTokenDao.getConnection());
                events = eventDao.getAll(username);

            }
            catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                eventDao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Close connection & return a response
            eventDao.closeConnection(true);
            return new Response(new EventsResponse(events));
        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
    }
}
