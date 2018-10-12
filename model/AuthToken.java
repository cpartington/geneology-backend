package model;

/**
 * Auth token data class.
 */
public class AuthToken extends Model {

    /** Authentication token */
    private String token;

    /** Name of the user */
    private String username;

    /** Success constructor */
    public AuthToken(String username) {
        this.username = username;
        this.token = createToken();
    }

    public AuthToken(String token, String username) {
        this.username = username;
        this.token = token;
    }

    /**
     * Create an auth token.
     * @return the generated auth token string
     */
    private String createToken() {
        return super.generateID();
    }

    // Getters & Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
