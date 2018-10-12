package service;

import java.util.ArrayList;
import java.util.logging.*;

import dao.Dao.*;
import dao.EventDao;
import dao.PersonDao;
import dao.UserDao;
import model.Event;
import model.Person;
import model.User;
import request.LoadRequest;
import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;

//TODO fix multi-level try structure

/**
 * Service that loads data into the database.
 */
public class LoadService extends Service {

    /**
     * Clears all data from the database and then loads the requested User, Person, and Event data.
     *
     * @param request a Request object containing the data to load into the database
     * @return a Response object detailing message or error
     */
    public Response load(LoadRequest request) {
        UserDao userDao = new UserDao();
        PersonDao personDao = null;
        EventDao eventDao = null;
        int userCount = 0;
        int personCount = 0;
        int eventCount = 0;

        if (!isValidRequest(request)) {
            return new ErrorResponse("Invalid request body.", "user");
        }

        try {
            // Open connection
            userDao.openConnection();
            try {
                // Clear the database
                userDao.clear();

                // Load all users
                userCount = addUsers(userDao, request.getUsers());

                // Load all persons
                personDao = new PersonDao(userDao.getConnection());
                personCount = addPersons(personDao, request.getPersons());


                // Load all events
                eventDao = new EventDao(personDao.getConnection());
                eventCount = addEvents(eventDao, request.getEvents());

                // Make sure data is valid
                if (userCount == INVALID_DATA || personCount == INVALID_DATA || eventCount == INVALID_DATA) {
                    userDao.closeConnection(false);
                    return new ErrorResponse("Invalid request body.", "user");
                }

            }
            catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                userDao.closeConnection(false);
                if (e.getMessage().startsWith("Already exists"))
                    return new ErrorResponse("Invalid request body.", "user");
                else 
                	return new ErrorResponse("Internal server error.", "server");
            }

            // Close connection, commit, & return successfully
            eventDao.closeConnection(true);
            return new SuccessResponse(makeResponse(userCount, personCount, eventCount));

        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorResponse("Internal server error.", "server");
        }
        finally {
            logger.exiting("LoadService", "load");
        }
    }

    /**
     * Adds users to the database.
     *
     * @param userDao the user dao
     * @param users the users to be added
     * @return the number of users added
     * @throws DatabaseException if users fail to add
     */
    protected int addUsers(UserDao userDao, ArrayList<User> users) throws DatabaseException {
        int userCount = 0;

        for (User user : users) {
            if (!isValidUser(user)) {
                return INVALID_DATA;
            }
            userDao.add(user);
            userCount++;
        }
        return userCount;
    }

    /**
     * Adds persons to the database.
     *
     * @param personDao the person dao
     * @param persons an array of persons to be added
     * @return the number of persons added
     * @throws DatabaseException if any persons fail to add
     */
    protected int addPersons(PersonDao personDao, ArrayList<Person> persons) throws DatabaseException {
        int personCount = 0;

        for (Person person : persons) {
            if (!isValidPerson(person)) {
                return INVALID_DATA;
            }
            personDao.add(person);
            personCount++;
        }
        return personCount;
    }

    /**
     * Adds events to the database.
     *
     * @param eventDao the event DAO
     * @param events an array of events to be added
     * @return the number of events added
     * @throws DatabaseException if any events fail to add
     */
    protected int addEvents(EventDao eventDao, ArrayList<Event> events) throws DatabaseException {
        int eventCount = 0;

        for (Event event : events) {
            if (!isValidEvent(event)) {
                return INVALID_DATA;
            }
            eventDao.add(event);
            eventCount++;
        }
        return eventCount;
    }

    private boolean isValidRequest(LoadRequest loadRequest) {
        if (loadRequest.getUsers() == null || loadRequest.getPersons() == null ||
                loadRequest.getEvents() == null)
            return false;
        else return true;
    }
    
    private String makeResponse(int users, int persons, int events) {
    	return String.format("Successfully added %d users, %d persons, and %d events to the database.",
                users, persons, events);
    }
}
