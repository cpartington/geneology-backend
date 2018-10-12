package model;

/**
 * User data class.
 */
public class User extends Model {

    /** User's username */
    private String userName;

    /** User's password */
    private String password;

    /** User's email */
    private String email;

    /** User's first name */
    private String firstName;

    /** User's last name */
    private String lastName;

    /** User's gender */
    private String gender;

    /** Unique person ID assigned to this user */
    private String personID;

    /** Generic constructor */
    public User(String username, String password, String email, String firstName, String lastName,
                String gender, String personID) {
        this.userName = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender.substring(0, 1).toLowerCase();;
        this.personID = personID;
    }

    /** Success constructor */
    public User(String username, String password, String email, String firstName, String lastName,
                String gender) {
        this.userName = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender.substring(0, 1).toLowerCase();;
    }

    // Getters & Setters
    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}
