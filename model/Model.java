package model;

import java.util.UUID;

/**
 * Generic model class.
 */
public class Model {

    /** Success constructor */
    public Model() {

    }

    /**
     * Generates a unique ID string.
     *
     * @return the created string
     */
    public String generateID() {
        return UUID.randomUUID().toString();
    }

}
