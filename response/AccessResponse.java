package response;

/**
 * Response sent by the LoginService.
 */
public class AccessResponse extends Response {

    /** Authorization token for the user's session */
    private String authToken;

    /** Username of the user */
    private String username;

    /** PersonID assigned to the user */
    private String personID;

    /** Success constructor */
    public AccessResponse(String authToken, String username, String personID) {
        this.authToken = authToken;
        this.username = username;
        this.personID = personID;
    }

    // Getters
    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getPersonID() {
        return personID;
    }
}
