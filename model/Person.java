package model;

/**
 * Person data class.
 */
public class Person extends Model {

    /** Unique identifier */
    private String personID;

    /** User to which this person belongs */
    private String descendant;

    /** Person's first name */
    private String firstName;

    /** Person's last name */
    private String lastName;

    /** Person's gender */
    private String gender;

    /** ID of person's father */
    private String father;

    /** ID of person's mother */
    private String mother;

    /** ID of person's spouse */
    private String spouse;

    /** New constructor */
    public Person(String username, String firstName, String lastName, String gender, String father,
                  String mother, String spouse) {
        this.descendant = username;
        this.personID = super.generateID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender.substring(0, 1).toLowerCase();
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
    }

    /** Existing constructor */
    public Person(String personID, String username, String firstName, String lastName,
                  String gender, String father, String mother, String spouse) {
        this.descendant = username;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender.substring(0, 1).toLowerCase();
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
    }

    /** Unknown person ID constructor */
    public Person(String username, String firstName, String lastName, String gender) {
        this.descendant = username;
        this.personID = super.generateID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender.substring(0, 1).toLowerCase();
    }

    /** Known person ID constructor */
    public Person(String personID, String username, String firstName, String lastName, String gender) {
        this.descendant = username;
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender.substring(0, 1).toLowerCase();
    }

    // Getters & Setters
    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getDescendant() {
        return descendant;
    }

    public String getUsername() {
        return descendant;
    }

    public void setDescendant(String descendant) {
        this.descendant = descendant;
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

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }
}
