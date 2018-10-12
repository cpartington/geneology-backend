package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

/**
 * User data access.
 */
public class UserDao extends Dao {

    /** Default constructor */
    public UserDao() {

    }

    /** Constructor that passes an existing connection */
    public UserDao(Connection connection) {
        super.connection = connection;
    }

   /**
     * Adds a user to the database.
     *
     * @param user the User object to be added to the database
     * @throws DatabaseException if the SQL update fails
     */
    public void add(User user) throws DatabaseException {
        final int SUCCESS = 1;

        try {
            PreparedStatement preparedStatement = null;
            try {
                // Create update
                String sql = "insert into Users (Username, Password, EmailAddress, FirstName, " +
                                                "LastName, Gender, PersonID)" +
                             "values (?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.setString(4, user.getFirstName());
                preparedStatement.setString(5, user.getLastName());
                preparedStatement.setString(6, user.getGender());
                preparedStatement.setString(7, user.getPersonID());

                // Execute update
                if (preparedStatement.executeUpdate() != SUCCESS)
                    throw new DatabaseException("Failed to add the user: Could not insert user.");
            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
            }

        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to add the user.", e);
        }

    }


    /**
     * Get the user associated with a username.
     *
     * @param username the username of the user
     * @return a user object
     */
    public User get(String username) throws DatabaseException {
        User user = null;
       int rowCount = 0;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select * from Users where Username = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);

                // Execute query
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    user = new User(resultSet.getString("Username"),
                            resultSet.getString("Password"),
                            resultSet.getString("EmailAddress"),
                            resultSet.getString("FirstName"),
                            resultSet.getString("LastName"),
                            resultSet.getString("Gender"),
                            resultSet.getString("PersonID"));
                    rowCount++;
                }
                if (rowCount != 1)
                    throw new SQLException("Invalid result set.");
            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (resultSet != null)
                    resultSet.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to obtain user.", e);
        }

        // Return
        return user;
    }


    /**
     * Checks to see if a user already exists in the database.
     *
     * @param username the name of the User to be checked for
     * @return true if the user exists
     * @throws DatabaseException if the SQL query fails
     */
    public boolean find(String username) throws DatabaseException {
        boolean found = false;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select * from Users " +
                             "where Username = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);

                // Execute query
                resultSet = preparedStatement.executeQuery();
                if (resultSet.isBeforeFirst())
                    found = true;
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
            }

        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to execute find.", e);
        }

        // Return
        return found;
    }


    /**
     * Checks to see if the user's provided username and password match a combination in the
     * database.
     *
     * @param username the login provided by the user
     * @param password the password provided by the user
     * @return true if user and password match in the database
     * @throws DatabaseException if the SQL query fails
     */
    public boolean checkPassword(String username, String password) throws DatabaseException {
        boolean match = false;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                // Create the query
                String sql = "select * from Users " +
                             "where Username = ? and Password = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                // Execute the query
                resultSet = preparedStatement.executeQuery();
                if (resultSet.isBeforeFirst())
                    match = true;
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to check password.", e);
        }

        // Return
        return match;
    }


    /**
     * Finds and returns the person ID associated with a user.
     *
     * @param username the username of the user
     * @return the unique person ID
     * @throws DatabaseException if the SQL query fails
     */
    public String getPersonID(String username) throws DatabaseException {
        String personID = null;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            int rowCount = 0;
            try {
                // Create the query
                String sql = "select PersonID from Users " +
                             "where Username = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);

                // Execute the query
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    personID = resultSet.getString("PersonID");
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
            throw new DatabaseException("Failed to obtain person ID.", e);
        }

        // Return
        return personID;
    }

}
