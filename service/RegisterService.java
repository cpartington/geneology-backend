package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import dao.*;
import dao.Dao.*;
import model.AuthToken;
import model.Event;
import model.Person;
import model.User;
import request.RegisterRequest;
import response.AccessResponse;
import response.Response;

/**
 * Service to register the user.
 */
public class RegisterService extends Service {

    /**
     * Creates a new user account, generates 4 generations of ancestor data for the new user,
     * logs the user in, and returns an auth token.
     *
     * @param request a Request object containing register information
     * @return a Response object containing the username and new associated personID & auth token
     */
    public Response register(RegisterRequest request) {
        final int DEFAULT_GENS = 4;
        UserDao userDao = new UserDao();
        PersonDao personDao = null;
        EventDao eventDao = null;
        AuthTokenDao authTokenDao = null;
        User user;
        Person person;
        String username;
        AuthToken authToken;

        logger.entering("RegisterService", "register");

        // Make sure request is valid
        if (!isValidRequest(request)) {
            return new Response("Invalid request body.", "user");
        }

        try {
            // Open connection
            userDao.openConnection();

            try {
                // Check if user already exists
                username = request.getUsername();
                if (userDao.find(username)) {
                    userDao.closeConnection(false);
                    return new Response("Username already exists.", "user");
                }

                // Create new account
                user = new User(username, request.getPassword(), request.getEmail(),
                                     request.getFirstName(), request.getLastName(), request.getGender());
                person = new Person(username, user.getFirstName(), user.getLastName(), user.getGender());
                user.setPersonID(person.getPersonID());
                if (!isValidUser(user) || !isValidPerson(person)) {
                    return new Response("Invalid request body.", "user");
                }
                userDao.add(user);
                personDao = new PersonDao(userDao.getConnection());

                // Generate data
                ArrayList<Person> persons;
                ArrayList<Event> events;
                try {
                    Generator generator = new Generator();
                    generator.generatePersons(person, DEFAULT_GENS);
                    persons = generator.getAllPersons();
                    persons.add(person); // add original person
                    events = generator.getAllEvents();
                }
                catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    userDao.closeConnection(false);
                    return new Response("Internal server error.", "server");
                }

                // Add to database with LoadService functions
                LoadService loadService = new LoadService();
                int personCount = loadService.addPersons(personDao, persons);
                eventDao = new EventDao(personDao.getConnection());
                int eventCount = loadService.addEvents(eventDao, events);

                if (personCount == INVALID_DATA || eventCount == INVALID_DATA) {
                    return new Response("Invalid generated data.", "server");
                }

                // Log in user
                authToken = new AuthToken(username);
                authTokenDao = new AuthTokenDao(eventDao.getConnection());
                authTokenDao.add(authToken);

            } catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                userDao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Close connection & return success
            authTokenDao.closeConnection(true);
            return new Response(new AccessResponse(authToken.getToken(), username, person.getPersonID()));
        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
        finally {
            logger.exiting("RegisterService", "register");
        }
    }

    private boolean isValidRequest(RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getPassword() == null ||
                registerRequest.getEmail() == null || registerRequest.getFirstName() == null ||
                registerRequest.getLastName() == null || registerRequest.getGender() == null)
            return false;
        else return true;
    }
}
