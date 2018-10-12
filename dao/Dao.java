package dao;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Superclass containing generic DAO methods.
 */
public class Dao {

    protected static Logger logger;

    static {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * Exceptions for errors in connecting/disconnecting to the database.
     */
    @SuppressWarnings("serial")
    public static class DatabaseException extends Exception {

        public DatabaseException(String message) {
            super(message);
        }

        public DatabaseException(String message, Exception e) {
            super(message, e);
        }
    }


    /**
     * Exception if attempting to access data that doesn't exist.
     */
    @SuppressWarnings("serial")
    public static class DoesNotExistException extends Exception {

        public DoesNotExistException(String message) {
            super(message);
        }
    }


    /** Import SQLite driver. */
    static {
        try {
            final String driver = "org.sqlite.JDBC";
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Connection to be made between the DAO and the database. */
    protected Connection connection;


    /**
     * Opens a database connection and starts a transaction.
     *
     * @throws DatabaseException if opening the connection fails
     */
    public void openConnection() throws DatabaseException {
        String dbName = "backend" + File.separator + "db" + File.separator + "database.sqlite";
        String connectionURL = "jdbc:sqlite:" + dbName;

        try {
            // Open a database connection
            connection = DriverManager.getConnection(connectionURL);

            // Start a transaction
            connection.setAutoCommit(false);
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Failed to open connection.", e);
        }

    }

    /**
     * Closes a database connection and commits or rolls back the transaction.
     *
     * @param commit whether to complete the transaction
     * @throws DatabaseException if connection fails to close
     */
    public void closeConnection(boolean commit) throws DatabaseException {
        try {
            if (commit)
                connection.commit();
            else
                connection.rollback();
            connection.close();
            connection = null;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Failed to close the connection.", e);
        }
    }

    /**
     * Clears the database, leaving empty tables.
     *
     * @throws DatabaseException if the SQL code fails
     */
    public void clear() throws DatabaseException {
        try {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                // Empty the tables
                statement.executeUpdate("delete from Users");
                statement.executeUpdate("delete from Persons");
                statement.executeUpdate("delete from Events");
                statement.executeUpdate("delete from AuthTokens");
            }
            finally {
                if (statement != null) {
                    statement.close();
                }
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Failed to clear tables.", e);
        }
    }

    /**
     * Creates database tables.
     */
    public void createTables() throws DatabaseException {
        try {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                statement.executeUpdate("create table if not exists Users (" +
                                            "Username       text not null unique," +
                                            "Password       text not null," +
                                            "EmailAddress   text not null," +
                                            "FirstName      text not null," +
                                            "LastName       text not null," +
                                            "Gender         text not null," +
                                            "PersonID       tet not null unique," +
                                            "primary key(Username) " +
                                            ")");
                statement.executeUpdate("create table if not exists Persons (" +
                                            "PersonID       text not null unique," +
                                            "Descendant     text not null," +
                                            "FirstName      text not null," +
                                            "LastName       text not null," +
                                            "Gender         text not null," +
                                            "Father         text," +
                                            "Mother         text," +
                                            "Spouse         text," +
                                            "primary key(PersonID) " +
                                            ")");
                statement.executeUpdate("create table if not exists Events (" +
                                            "EventID        text not null unique," +
                                            "Descendant     text not null," +
                                            "PersonID       text not null," +
                                            "Latitude       text not null," +
                                            "Longitude      text not null," +
                                            "Country        text not null," +
                                            "City           text not null," +
                                            "EventType      text not null," +
                                            "Year           text not null," +
                                            "primary key(EventID)" +
                                            ")");
                statement.executeUpdate("create table if not exists AuthTokens (" +
                                            "Token      text not null unique," +
                                            "Username   text not null," +
                                            "primary key(Token)" +
                                            ")");
            }
            finally {
                if (statement != null) {
                    statement.close();
                }
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Failed to create tables.", e);
        }
    }

    /**
     * Fills the database with new data.
     *
     * @throws DatabaseException if the SQL code fails
     */
    public void fill(String username, int generations) throws DatabaseException {
        // hypothetically delete this function
    }

    /**
     * Clears the database and loads new data.
     *
     * @throws DatabaseException if the SQL code fails
     */
    public void load() throws DatabaseException {
        // hypothetically delete this function
    }

    // Getters
    public Connection getConnection() {
        return connection;
    }
}
