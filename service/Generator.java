package service;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import model.*;

/**
 * Generates people and events for a user.
 */
public class Generator {

    /** Object for json conversion. */
    public class NameList {
        ArrayList<String> data;

        public NameList() {
            data = new ArrayList<>();
        }
    }

    /** Object for json conversion. */
    public class PlaceList {
        ArrayList<Place> data;

        public PlaceList() {
            data = new ArrayList<>();
        }
    }

    /** Average number of years between each generation */
    private static final int GENERATION = 25;

    /** An array of all generated persons */
    private ArrayList<Person> allPersons;

    /** An array of all generated events */
    private ArrayList<Event> allEvents;

    /** Expected number of generations */
    private int expectedGens;

    /** Default constructor */
    public Generator() throws IOException {
        allPersons = new ArrayList<>();
        allEvents = new ArrayList<>();
        random = new Random();
        readData();
    }

    /** Reads in .json files to create data. */
    private void readData() throws IOException {
        Gson gson = new Gson();

        // File names
        String path = "backend" + File.separator + "db" + File.separator;
        String fNames = path + "fnames.json";
        String mNames = path + "mnames.json";
        String sNames = path + "snames.json";
        String placeJ = path + "locations.json";

        // Import as objects
        femaleNames = gson.fromJson(new FileReader(fNames), NameList.class);
        maleNames = gson.fromJson(new FileReader(mNames), NameList.class);
        surnames = gson.fromJson(new FileReader(sNames), NameList.class);
        places = gson.fromJson(new FileReader(placeJ), PlaceList.class);
    }

    /**
     * Generates persons for a user.
     *
     * @param person a Person object to be the root of the family tree
     * @param generations the number of generations to create
     */
    public void generatePersons(Person person, int generations) {
        final int MIN_YEAR = 1990;
        final int MAX_YEAR = 2000;

        // Randomly determine year of birth of user
        int rootBirthYear = getYear(MIN_YEAR, MAX_YEAR);

        // Set the expected number of generations
        expectedGens = generations;

        // Generate birth event for current person
        Event birth = new Event(person.getUsername(), person.getPersonID(), getPlace(),
                "Birth", Integer.toString(rootBirthYear));
        allEvents.add(birth);

        // Recursively generate parents
        getParents(person, rootBirthYear - GENERATION, 1);
    }

    /**
     * Generates parents for a person.
     *
     * @param person a Person object to be the root of the family tree
     * @param avgBirthYear the "average" year of birth of persons generated
     */
    private void getParents(Person person, int avgBirthYear, int currentGen) {
        // Make sure generation should be continuing
        if (currentGen > expectedGens)
            return;

        // Create parents
        Person mother = new Person(person.getUsername(), getFemaleName(), getSurname(), "F");
        Person father = new Person(person.getUsername(), getMaleName(), person.getLastName(), "M");
        mother.setSpouse(father.getPersonID());
        father.setSpouse(mother.getPersonID());
        allPersons.add(mother);
        allPersons.add(father);

        // Update child
        person.setMother(mother.getPersonID());
        person.setFather(father.getPersonID());

        // Create events for each parent
        int motherBirth = generateEvents(mother, avgBirthYear, currentGen);
        int fatherBirth = generateEvents(father, avgBirthYear, currentGen);
        // Marriage event
        int marriageYear = getMarriageYear(avgBirthYear + GENERATION);
        Event motherMarriage = new Event(person.getUsername(), mother.getPersonID(), getPlace(),
                "Marriage", Integer.toString(marriageYear));
        Event fatherMarriage = new Event(person.getUsername(), father.getPersonID(),
                motherMarriage.getLatitude(), motherMarriage.getLongitude(), motherMarriage.getCountry(),
                motherMarriage.getCity(), "Marriage", motherMarriage.getYear());
        allEvents.add(motherMarriage);
        allEvents.add(fatherMarriage);

        // Create parents for each parent
        getParents(mother, motherBirth - GENERATION, currentGen + 1);
        getParents(father, fatherBirth - GENERATION, currentGen + 1);
    }

    /** Generates female first name. */
    private String getFemaleName() {
        int index = random.nextInt(femaleNames.data.size());
        return femaleNames.data.get(index);
    }

    /** Generates male first name. */
    private String getMaleName() {
        int index = random.nextInt(maleNames.data.size());
        return maleNames.data.get(index);
    }

    /** Generates surname. */
    private String getSurname() {
        int index = random.nextInt(surnames.data.size());
        return surnames.data.get(index);
    }

    /** Gets the year of marriage. */
    private int getMarriageYear(int childBirthYear) {
        final int RANGE = 5;
        int randomDifference = random.nextInt(RANGE) - 1;
        return childBirthYear - randomDifference;
    }

    /**
     * Generates events for a list of persons.
     *
     * @param person the person needing events
     * @param avgBirthYear the generic birth year for the person
     * @param currentGen the current generation
     */
    private int generateEvents(Person person, int avgBirthYear, int currentGen) {
        final int RANGE = 10;
        final int NO_DEATH = -1;
        final int THIS_YEAR = 2018;
        int minYear;
        int maxYear;

        // Generate birth year
        minYear = random.nextInt(RANGE + 1) + (avgBirthYear - 5);
        Place birthPlace = getPlace();
        Place deathPlace;
        Event birthEvent = new Event(person.getUsername(), person.getPersonID(), birthPlace,
                "Birth", Integer.toString(minYear));
        allEvents.add(birthEvent);

        // Determine if person has died
        maxYear = generateDeath(currentGen, minYear);

        // Generate random events
        if (maxYear == NO_DEATH)
            deathPlace = generateRandomEvents(person, minYear, THIS_YEAR, birthEvent);
        else
            deathPlace = generateRandomEvents(person, minYear, maxYear, birthEvent);

        // Generate death if necessary
        if (maxYear != NO_DEATH) {
            Event deathEvent = new Event(person.getUsername(), person.getPersonID(),
                    getPlace(deathPlace.getLatitude(), deathPlace.getLongitude()),
                    "Death", Integer.toString(maxYear));
            allEvents.add(deathEvent);
        }

        return minYear;
    }

    /** Generate random events and return the last one. */
    private Place generateRandomEvents(Person person, int birthYear, int maxYear, Event birthEvent) {
        final int NUM_EVENTS = 3;               // Number of events to generate for each person

        Place place = new Place();          // Place for the event
        String eventType;                   // Type of event
        Event event;                        // The generated event
        int index;                          // Random index to choose eventType
        int year;                           // The year the event took place

        int count = 0;                      // Keeps track of how many events have been created
        Event lastEvent = birthEvent;       // Keeps track of the last created event
        int partition = (maxYear - birthYear) / NUM_EVENTS;   // Provides a range of years for each event
        int minForEvent = birthYear;        // Establishes a minimum year for each event

        while (count < NUM_EVENTS) {

            // Build the pieces
            index = random.nextInt(events.length); // randomly choose an event
            eventType = events[index];
            place = getPlace(lastEvent.getLatitude(), lastEvent.getLongitude()); // choose a location
            year = getYear(minForEvent, minForEvent + partition); // choose a year

            // Create the event
            event = new Event(person.getUsername(), person.getPersonID(), place, eventType,
                    Integer.toString(year));
            allEvents.add(event);
            lastEvent = event;

            // Increment
            count++;
            minForEvent += partition;
        }

        return place;
    }

    /** Generate random year. */
    private int getYear(int minYear, int maxYear) {
        return random.nextInt(maxYear - minYear + 1) + minYear;
    }

    /** Generate year of death. */
    private int generateDeath(int currentGen, int birthYear) {
        final int RANGE = 10;
        final int THIS_YEAR = 2018;
        final int MIN_DEATH = 64;
        final int MAX_DEATH = 102;
        int maxYear = 0;
        int minYear = 0;
        int deathYear = 0;

        // Set min & max year parameters
        if (birthYear + MAX_DEATH > THIS_YEAR)
            maxYear = THIS_YEAR;
        else
            maxYear = birthYear + MAX_DEATH;
        if (birthYear + MIN_DEATH > THIS_YEAR)
            minYear = THIS_YEAR;
        else
            minYear = birthYear + MIN_DEATH;

        // Determine if person is dead
        if (currentGen == 1) {
            return -1;
        }
        if (currentGen == 2) {
            if (!(random.nextInt(RANGE + 1) >= 9))
                return -1;
        }
        else if (currentGen == 3) {
            if (!(random.nextInt(RANGE + 1) >= 3))
                return -1;
        }
        deathYear = random.nextInt(maxYear - minYear + 1) + minYear;
        return deathYear;
    }

    /** Generate location of event. */
    private Place getPlace() {
        int index = random.nextInt(places.data.size());
        return places.data.get(index);
    }

    /** Generate location of event given previous event. */
    private Place getPlace(double prevLatitude, double prevLongitude) {
        final double GOOD_DISTANCE = 10;
        Set<Place> potentials = new HashSet<>();
        double longDiff;
        double latDiff;

        for (Place place : places.data) {
            latDiff  = Math.abs(place.getLatitude() - prevLatitude);
            longDiff = Math.abs(place.getLongitude() - prevLongitude);
            if (latDiff <= GOOD_DISTANCE && longDiff <= GOOD_DISTANCE) {
                potentials.add(place);
            }
        }
        if (potentials.size() > 1) {
            Place placeArray[] = potentials.toArray(new Place[potentials.size()]);
            int index = random.nextInt(potentials.size());
            return placeArray[index];
        }
        else {
            return getPlace();
        }
    }

    // Getters & Setters
    public ArrayList<Person> getAllPersons() {
        return allPersons;
    }

    public ArrayList<Event> getAllEvents() {
        return allEvents;
    }

    public NameList getFemaleNames() {
        return femaleNames;
    }

    public NameList getMaleNames() {
        return maleNames;
    }

    public NameList getSurnames() {
        return surnames;
    }

    public PlaceList getPlaces() {
        return places;
    }

    /** Array of random events */
    private String events[] = {"Expensive boat trip", "Invents the wheel", "Adopts a puppy",
                        "Gets hit by a car", "Eats chocolate", "Joins a cult",
                        "Cooks a hot dog", "Learns to read", "Learns to drive",
                        "Buys a new shirt", "Tries sushi", "Wins the lottery",
                        "Paints a landscape", "Goes backpacking", "Finds a lucky coin",
                        "Visits family", "Discovers long-lost sibling", "Goes on a bad date",
                        "Gets Knighted"};

    /** Array of female names */
    private NameList femaleNames;

    /** Array of male names */
    private NameList maleNames;

    /** Array of surnames */
    private NameList surnames;

    /** Array of places */
    private PlaceList places;

    private Random random;
}
