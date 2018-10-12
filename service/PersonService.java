package service;

import java.util.ArrayList;
import java.util.logging.Level;

import dao.AuthTokenDao;
import dao.Dao.*;
import dao.PersonDao;
import model.Person;
import response.PersonResponse;
import response.PersonsResponse;
import response.Response;

/**
 * Service to obtain Person data.
 */
public class PersonService extends Service {

    /**
     * Returns the single Person object with the specified ID.
     *
     * @param personID the ID of the desired Person
     * @return a Response object with the Person connected with the personID
     */
    public Response person(String personID, String authToken) {
        AuthTokenDao authTokenDao = new AuthTokenDao();
        PersonDao personDao = null;
        String authUsername;
        Person person;

        logger.entering("PersonResponse", "person");

        try {
            // Open connection
            authTokenDao.openConnection();
            try {
                // Check that the auth token is valid
                try {
                    authUsername = authTokenDao.getUsername(authToken);
                }
                catch (DoesNotExistException e) {
                    authTokenDao.closeConnection(false);
                    return new Response("Invalid auth token.", "user");
                }

                // Get the person
                personDao = new PersonDao(authTokenDao.getConnection());

                try {
                    person = personDao.get(personID);
                }
                catch (DoesNotExistException e) {
                    personDao.closeConnection(false);
                    return new Response("Invalid personID parameter.", "user");
                }

                // Make sure auth token and user match up
                if (!person.getDescendant().equals(authUsername)) {
                    personDao.closeConnection(false);
                    return new Response("Requested person does not belong to this user.",
                                        "user");
                }
            }
            catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                personDao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Close connection & return successfully
            personDao.closeConnection(true);
            return new Response(new PersonResponse(person));

        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
        finally {
            logger.exiting("PersonResponse", "person");
        }

    }

    /**
     * Returns all family members of the current user.
     *
     * @param authToken the authToken associated with the current user
     * @return a Response object with an array of Persons associated with the current user
     */
    public Response persons(String authToken) {
        AuthTokenDao authTokenDao = new AuthTokenDao();
        PersonDao personDao = null;
        ArrayList<Person> persons;
        String username = null;

        logger.entering("PersonsService", "persons");

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

                // Get Persons associated with the user
                personDao = new PersonDao(authTokenDao.getConnection());
                persons = personDao.getAll(username);

            }
            catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                authTokenDao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Close connection & return a response
            personDao.closeConnection(true);
            return new Response(new PersonsResponse(persons));
        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
        finally {
            logger.exiting("PersonsService", "persons");
        }
    }

}
