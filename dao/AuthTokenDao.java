package dao;

import model.AuthToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * Auth token data access.
 */
public class AuthTokenDao extends Dao {

    /** Default constructor */
    public AuthTokenDao() {

    }

    /** Constructor that passes in an existing connection */
    public AuthTokenDao(Connection connection) {
        super.connection = connection;
    }

    /**
     * Adds an auth token to the database.
     *
     * @param authToken the auth token object to be added
     * @throws DatabaseException if SQL update fails to execute
     */
    public void add(AuthToken authToken) throws DatabaseException {
        final int SUCCESS = 1;

        try {
            PreparedStatement preparedStatement = null;
            try {
                // Create update
                String sql = "insert into AuthTokens (Token, Username) " +
                             "values (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, authToken.getToken());
                preparedStatement.setString(2, authToken.getUsername());

                // Create update
                if (preparedStatement.executeUpdate() != SUCCESS)
                    throw new DatabaseException("Failed to add the auth token: " +
                            "Could not insert auth token.");
            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (e.getMessage().startsWith("[SQLITE_CONSTRAINT]")) {
                throw new DatabaseException("Already exists: Auth token.", e);
            }
            else throw new DatabaseException("Failed to add the auth token.", e);
        }
    }

    /**
     * Gets the user associated with the provided auth token.
     *
     * @param authToken the auth token
     * @return the username of the user that the auth token belongs to
     * @throws DatabaseException if the SQL query fails
     * @throws DoesNotExistException if the authToken doesn't exist
     */
    public String getUsername(String authToken) throws DatabaseException, DoesNotExistException {
        // TODO getUsername
        String username = null;
        int rowCount = 0;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select Username from AuthTokens " +
                             "where Token = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, authToken);

                // Execute query
                resultSet = preparedStatement.executeQuery();

                // Check if any data was found
                if (!resultSet.isBeforeFirst()) {
                    throw new DoesNotExistException("Auth token does not exist.");
                }

                // Get the username
                while (resultSet.next()) {
                    username = resultSet.getString("Username");
                    rowCount++;
                }
                if (rowCount != 1)
                    throw new SQLException("Invalid result set.");
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to obtain username.", e);
        }

        // Return
        return username;
    }

}
