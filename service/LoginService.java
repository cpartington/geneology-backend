package service;

import java.util.logging.Level;

import dao.AuthTokenDao;
import dao.Dao.*;
import dao.UserDao;
import model.AuthToken;
import response.AccessResponse;
import request.LoginRequest;
import response.Response;

/**
 * Service to log in the user.
 */
public class LoginService extends Service {

    /**
     * Logs in the user and returns an auth token.
     *
     * @param request a Request object created by the login handler
     * @return a Response detailing message or error
     */
    public Response login(LoginRequest request) {
        UserDao userDao = new UserDao();
        AuthTokenDao authTokenDao;
        AuthToken newToken;
        String personID;
        String username = request.getUsername();
        String password = request.getPassword();

        logger.entering("LoginService", "login");

        // Make sure request is valid
        if (!isValidRequest(request)) {
            return new Response("Request property missing or has invalid value.", "user");
        }

        try {
            userDao.openConnection();
            try {

                // Make sure user exists
                if (!userDao.find(username)) {
                    userDao.closeConnection(false);
                    return new Response("Invalid username or password.", "user");
                }

                // Check username and password
                if (!userDao.checkPassword(username, password)) {
                    userDao.closeConnection(false);
                    return new Response("Invalid username or password.", "user");
                }

                // Get person ID
                personID = userDao.getPersonID(username);

                // Get auth token for the user
                newToken = new AuthToken(username);
                authTokenDao = new AuthTokenDao(userDao.getConnection());
                authTokenDao.add(newToken);

            } catch (DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                userDao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Close connection & return success message
            authTokenDao.closeConnection(true);
            return new Response(new AccessResponse(newToken.getToken(), username, personID));
        }
        catch (DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
        finally {
            logger.exiting("LoginResponse", "login");
        }
    }

    private boolean isValidRequest(LoginRequest loginRequest) {
        if (loginRequest.getUsername() == null || loginRequest.getPassword() == null)
            return false;
        else return true;
    }
}
