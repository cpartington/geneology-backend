package response;

import java.util.ArrayList;

import model.Person;

public class PersonsResponse extends Response {

    /** Array of Person objects */
    private ArrayList<Person> persons;

    /** Default constructor */
    public PersonsResponse() {

    }

    public PersonsResponse(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public ArrayList<Person> getPersons() {
        return persons;
    }
}
