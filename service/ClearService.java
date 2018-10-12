package service;

import java.util.logging.Level;

import dao.Dao;
import response.Response;

/**
 * Service to delete all data from the database.
 */
public class ClearService extends Service {

    /**
     * Deletes all data from the database, including user accounts, auth tokens, and generated
     * person & event data.
     *
     * @return a Response object identifying message or failure
     */
    public Response clear() {
        Dao dao = new Dao();

        logger.entering("ClearService", "clear");

        try {
            dao.openConnection();

            // Delete data
            try {
                dao.clear();
            } catch (Dao.DatabaseException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
                dao.closeConnection(false);
                return new Response("Internal server error.", "server");
            }

            // Close connection & return successful response
            dao.closeConnection(true);
            return new Response(true);

        }
        catch (Dao.DatabaseException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new Response("Internal server error.", "server");
        }
        finally {
            logger.exiting("ClearService", "clear");
        }
    }
}
