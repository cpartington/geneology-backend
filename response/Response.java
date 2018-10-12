package response;

/**
 * Response object containing various types of responses.
 */
public class Response {

    // RESPONSE TYPES //

    /** Error message if service is unsuccessful */
    private ErrorResponse errorResponse;

    /** Success message is service is successful */
    private SuccessResponse successResponse;

    /** Single event response */
    private EventResponse eventResponse;

    /** Multi-person response */
    private EventsResponse eventsResponse;

    /** Login / register response */
    private AccessResponse accessResponse;

    /** Person response */
    private PersonResponse personResponse;

    /** Multi-person response */
    private  PersonsResponse personsResponse;

    /** Determines the type of error */
    private String errorType;

    /** Boolean indicating success or fail */
    private boolean success;

    // CONSTRUCTORS //

    /** Default constructor */
    public Response() {
        success = true;
    }

    /** Error constructor */
    public Response(String error, String errorType) {
        this.errorResponse = new ErrorResponse(error);
        this.errorType = errorType;
        success = false;
    }

    /** Clear message constructor */
    public Response(boolean noError) {
        this.successResponse = new SuccessResponse("Clear succeeded.");
        success = true;
    }

    /** Fill message constructor */
    public Response(int persons, int events) {
        String message = String.format("Successfully added %d persons and %d events to the database.",
                persons, events);
        this.successResponse = new SuccessResponse(message);
        success = true;
    }

    /** Load message constructor */
    public Response(int users, int persons, int events) {
        String message = String.format("Successfully added %d users, %d persons, and %d events to the database.",
                users, persons, events);
        this.successResponse = new SuccessResponse(message);
        success = true;
    }

    /** Event response constructor */
    public Response(EventResponse eventResponse) {
        this.eventResponse = eventResponse;
        success = true;
    }

    /** Multi-event response constructor */
    public Response(EventsResponse eventsResponse) {
        this.eventsResponse = eventsResponse;
        success = true;
    }

    /** Register / login response constructor */
    public Response(AccessResponse accessResponse) {
        this.accessResponse = accessResponse;
        success = true;
    }

    /** Person response constructor */
    public Response(PersonResponse personResponse) {
        this.personResponse = personResponse;
        success = true;
    }

    /** Multi-person response constructor */
    public Response(PersonsResponse personsResponse) {
        this.personsResponse = personsResponse;
        success = true;
    }

    // GETTERS //

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public SuccessResponse getSuccessResponse() {
        return successResponse;
    }

    public EventResponse getEventResponse() {
        return eventResponse;
    }

    public EventsResponse getEventsResponse() {
        return eventsResponse;
    }

    public AccessResponse getAccessResponse() {
        return accessResponse;
    }

    public PersonResponse getPersonResponse() {
        return personResponse;
    }

    public PersonsResponse getPersonsResponse() {
        return personsResponse;
    }

    public String getErrorType() {
        return errorType;
    }

    public boolean isSuccess() {
        return success;
    }
}
