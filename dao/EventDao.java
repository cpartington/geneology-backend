package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import model.Event;

/**
 * Event data access.
 */
public class EventDao extends Dao {

    /** Default constructor */
    public EventDao() {

    }

    /** Constructor that passes an existing connection */
    public EventDao(Connection connection) {
        super.connection = connection;
    }

    /**
     * Adds an event to the database.
     *
     * @param event the event to be added
     * @throws DatabaseException if SQL update fails
     */
    public void add(Event event) throws DatabaseException {
        final int SUCCESS = 1;

        try {
            PreparedStatement preparedStatement = null;
            try {
                // Create update
                String sql = "insert into Events (EventID, Descendant, PersonID, Latitude, " +
                                                 "Longitude, Country, City, EventType, Year) " +
                             "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);

                // Set statement
                preparedStatement.setString(1, event.getEventID());
                preparedStatement.setString(2, event.getDescendant());
                preparedStatement.setString(3, event.getPersonID());
                preparedStatement.setDouble(4, event.getLatitude());
                preparedStatement.setDouble(5, event.getLongitude());
                preparedStatement.setString(6, event.getCountry());
                preparedStatement.setString(7, event.getCity());
                preparedStatement.setString(8, event.getEventType());
                preparedStatement.setString(9, event.getYear());

                // Execute update
                if (preparedStatement.executeUpdate() != SUCCESS)
                    throw new DatabaseException("Failed to add the event: Could not insert the event.");
            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (e.getMessage().startsWith("[SQLITE_CONSTRAINT]")) {
                throw new DatabaseException("Already exists: Event ID.", e);
            }
            else throw new DatabaseException("Failed to add the event.", e);
        }

    }

    /**
     * Finds an event and returns it.
     *
     * @param eventID the unique ID of the event
     * @return the event object
     * @throws DoesNotExistException if event does not exist
     * @throws DatabaseException if SQL query fails
     */
    public Event get(String eventID) throws DoesNotExistException, DatabaseException {
        Event event = null;
        int rowCount = 0;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select * from Events where EventID = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, eventID);

                // Execute query
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.isBeforeFirst())
                    throw new DoesNotExistException("Event does not exist.");

                while(resultSet.next()) {
                    event = new Event(resultSet.getString("EventID"),
                                      resultSet.getString("Descendant"),
                                      resultSet.getString("PersonID"),
                                      resultSet.getDouble("Latitude"),
                                      resultSet.getDouble("Longitude"),
                                      resultSet.getString("Country"),
                                      resultSet.getString("City"),
                                      resultSet.getString("EventType"),
                                      resultSet.getString("Year"));
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
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Failed to obtain event.", e);
        }

        // Return
        return event;
    }

    /**
     * Find all events associated with all family members of a user.
     *
     * @param descendant the username of the user
     * @return an array of event objects
     * @throws DatabaseException if SQL query fails
     */
    public ArrayList<Event> getAll(String descendant) throws DatabaseException {
        ArrayList<Event> events = new ArrayList<>();
        Event event = null;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select * from Events where Descendant = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, descendant);

                // Execute query
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    event = new Event(resultSet.getString("EventID"),
                                    resultSet.getString("Descendant"),
                                    resultSet.getString("PersonID"),
                                    resultSet.getDouble("Latitude"),
                                    resultSet.getDouble("Longitude"),
                                    resultSet.getString("Country"),
                                    resultSet.getString("City"),
                                    resultSet.getString("EventType"),
                                    resultSet.getString("Year"));
                    events.add(event);
                }
            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (resultSet != null)
                    resultSet.close();
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new DatabaseException("Failed to get all events for the user.", e);
        }

        return events;
    }

    /**
     * Deletes all Event data associated with a user.
     *
     * @param descendant the username of the user
     * @throws DatabaseException if the SQL update fails
     */
    public void deleteAll(String descendant) throws DatabaseException {
        final int SUCCESS = 1;

        try {
            PreparedStatement preparedStatement = null;
            try {
                String sql = "delete from Events where Descendant = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, descendant);

                // Execute update
                preparedStatement.executeUpdate();
            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Failed to delete events.", e);
        }
    }

}
