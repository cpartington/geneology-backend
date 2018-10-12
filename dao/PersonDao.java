package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import model.Person;

/**
 * Person data access.
 */
public class PersonDao extends Dao {

    /** Default constructor */
    public PersonDao() {

    }

    /** Constructor that passes an existing connection */
    public PersonDao(Connection connection) {
        super.connection = connection;
    }

    /**
     * Adds a Person to the database.
     *
     * @param person the Person to be added
     * @throws DatabaseException if the SQL update fails
     */
    public void add(Person person) throws DatabaseException {
        final int SUCCESS = 1;

        try {
            PreparedStatement preparedStatement = null;
            try {
                // Create update
                String sql = "insert into Persons (PersonID, Descendant, FirstName, LastName," +
                                                  "Gender, Mother, Father, Spouse) " +
                             "values (?, ?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);

                // Set statement
                preparedStatement.setString(1, person.getPersonID());
                preparedStatement.setString(2, person.getDescendant());
                preparedStatement.setString(3, person.getFirstName());
                preparedStatement.setString(4, person.getLastName());
                preparedStatement.setString(5, person.getGender());
                preparedStatement.setString(6, person.getMother());
                preparedStatement.setString(7, person.getFather());
                preparedStatement.setString(8, person.getSpouse());

                // Execute update
                if (preparedStatement.executeUpdate() != SUCCESS)
                    throw new DatabaseException("Failed to add the person: Could not insert person.");

            }
            finally {
                if (preparedStatement != null)
                    preparedStatement.close();
            }
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            if (e.getMessage().startsWith("[SQLITE_CONSTRAINT]")) {
                throw new DatabaseException("Already exists: Person ID.", e);
            }
            else throw new DatabaseException("Failed to add the person.", e);
        }
    }


    /**
     * Finds and returns a Person associated with a person ID.
     *
     * @param personID the unique person ID
     * @return a Person object
     * @throws DoesNotExistException if person ID does not exist
     * @throws DatabaseException if SQL query fails
     */
    public Person get(String personID) throws DoesNotExistException, DatabaseException {
        Person person = null;
        int rowCount = 0;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select * from Persons where PersonID = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, personID);

                // Execute query
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.isBeforeFirst())
                    throw new DoesNotExistException("Person does not exist.");

                while (resultSet.next()) {
                    person = new Person(resultSet.getString("PersonID"),
                                        resultSet.getString("Descendant"),
                                        resultSet.getString("FirstName"),
                                        resultSet.getString("LastName"),
                                        resultSet.getString("Gender"),
                                        resultSet.getString("Father"),
                                        resultSet.getString("Mother"),
                                        resultSet.getString("Spouse"));
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
            throw new DatabaseException("Failed to obtain person.", e);
        }

        // Return
        return person;
    }


    /**
     * Find all Persons associated with a User.
     *
     * @param descendant the username of the user
     * @return an array of Persons
     * @throws DatabaseException if the SQL query fails
     */
    public ArrayList<Person> getAll(String descendant) throws DatabaseException {
        ArrayList<Person> persons = new ArrayList<>();
        Person person;

        try {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                // Create query
                String sql = "select * from Persons where Descendant = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, descendant);

                //Execute query
                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    person = new Person(resultSet.getString("PersonID"),
                                        resultSet.getString("Descendant"),
                                        resultSet.getString("FirstName"),
                                        resultSet.getString("LastName"),
                                        resultSet.getString("Gender"),
                                        resultSet.getString("Father"),
                                        resultSet.getString("Mother"),
                                        resultSet.getString("Spouse"));
                    persons.add(person);
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
            throw new DatabaseException("Failed to obtain persons associated with user.", e);
        }

        return persons;
    }


    /**
     * Deletes all Person data associated with a user.
     *
     * @param descendant the username of the user whose data needs to be deleted
     * @throws DatabaseException if the SQL update fails
     */
    public void deleteAll(String descendant) throws DatabaseException {
        final int SUCCESS = 1;

        try {
            PreparedStatement preparedStatement = null;
            try {
                // Create update
                String sql = "delete from Persons where Descendant = ?";
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
            throw new DatabaseException("Failed to delete persons.", e);
        }
    }

}
