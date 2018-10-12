package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import dao.*;
import dao.Dao.*;
import model.Event;
import model.Person;
import model.User;
import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;

//TODO fix multi-level try structure

/**
 * Service to fill database with Persons and Events.
 */
public class FillService extends Service {

    /**
     * Populates the database with generated data for the specified username.
     *
     * @param username the username of the user requesting data
     * @param generations the number of generations to fill
     * @return a Response object with a message or error message
     */
    public Response fill(String username, int generations) {
        UserDao userDao = new UserDao();
        PersonDao personDao = null;
        EventDao eventDao = null;
        User user;
        Person person;
        String personID;
        int personCount = 0;
        int eventCount = 0;

        logger.entering("FillService", "fill");

        // Make sure request is valid
        if (generations < 0) {
            return new ErrorResponse("Invalid generations parameter.", "user");
        }

        try {
            userDao.openConnection();
            try {
                // Check that user exists
                if (!userDao.find(username)) {
                    userDao.closeConnection(false);
                    return new ErrorResponse("Invalid username parameter.", "user");
                }
                user = userDao.get(username);

                // Delete all persons & events associated with user
                personDao = new PersonDao(userDao.getConnection());
                personDao.deleteAll(username);
                eventDao = new EventDao(personDao.getConnection());
                eventDao.deleteAll(username);

                // Create new person for user
                person = new Person(user.getPersonID(), user.getUsername(), user.getFirstName(),
                                    user.getLastName(), user.getGender());

                // Generate data
                ArrayList<Person> persons;
                ArrayList<Event> events;
                try {
                    Generator generator = new Generator();
                    generator.generatePersons(person, generations);
                    persons = generator.getAllPersons();
                    persons.add(person); // add original person
                    events = generator.getAllEvents();
                }
                catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    userDao.closeConnection(false);
                    return new ErrorResponse("Internal server error.", "server");
                }

                // Put data in database with LoadService functions
                LoadService loadService = new LoadService();
                personCount = loadService.addPersons(personDao, persons);
                eventDao = new EventDao(personDao.getConnection());
                eventCount = loadService.addEvents(eventDao, events);
            }
            catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                userDao.closeConnection(false);
                return new ErrorResponse("Internal server error.", "server");
            }

            // Close connection & return successfully
            eventDao.closeConnection(true);
            return new SuccessResponse(makeResponse(personCount, eventCount));

        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorResponse("Internal server error.", "server");
        }
        finally {
            logger.exiting("FillService", "fill");
        }
    }
    
    private String makeResponse(int persons, int events) {
    	return String.format("Successfully added %d persons and %d events to the database.", 
    			persons, events);
    }
}
