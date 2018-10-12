package service;

import java.util.logging.Level;
import java.util.logging.Logger;

import dao.Dao;
import model.Event;
import model.Person;
import model.User;

/**
 * Implements a request by calling DAO classes.
 */
public class Service {

    protected static final int INVALID_DATA = -1;

    protected static Logger logger;

    static {
        logger = Logger.getLogger("familymapserver");
    }

    public boolean isValidPerson(Person person) {
        if (person.getPersonID() == null || person.getFirstName() == null ||
                person.getLastName() == null || person.getGender() == null) {
            return false;
        }
        else if (!person.getGender().toUpperCase().equals("F") && !person.getGender().toUpperCase().equals("M")) {
            return false;
        }
        return true;
    }

    public boolean isValidEvent(Event event) {
        if (event.getEventID() == null || event.getPersonID() == null ||
                event.getCountry() == null || event.getCity() == null ||
                event.getEventType() == null || event.getYear() == null) {
            return false;
        }
        try {
            Integer.parseInt(event.getYear());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public boolean isValidUser(User user) {
        if (user.getPassword() == null || user.getEmail() == null || user.getFirstName() == null ||
                user.getLastName() == null || user.getGender() == null || user.getPersonID() == null) {
            return false;
        }
        else if (!user.getGender().toLowerCase().equals("f") && !user.getGender().toLowerCase().equals("m")) {
            return false;
        }
        return true;
    }

    public void createTables() {
        Dao dao = new Dao();

        logger.entering("Service", "createTables");

        try {
            dao.openConnection();

            // Create tables if needed
            try {
                dao.createTables();
            }
            catch (Dao.DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                dao.closeConnection(false);
            }

            // Close connection & return successful response
            dao.closeConnection(true);
        }
        catch (Dao.DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        finally {
            logger.exiting("Service", "createTables");
        }
    }

}
