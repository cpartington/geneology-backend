package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import response.ErrorResponse;
import response.PersonResponse;
import response.PersonsResponse;
import response.Response;
import service.PersonService;

public class PersonHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final int PERSON_ID_START = 8;
        Gson gson = new Gson();
        Response response = null;
        boolean persons = false;

        logger.entering("PersonHandler", "handle");

        // Get the HTTP request headers
        Headers requestHeaders = httpExchange.getRequestHeaders();

        if (!requestHeaders.containsKey("Authorization")) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(gson.toJson(new ErrorResponse("Invalid auth token.", "user")), responseBody);
            responseBody.close();
        }
        else {
            String authToken = requestHeaders.getFirst("Authorization");
            String urlPath = httpExchange.getRequestURI().getPath();
            PersonService personService = new PersonService();

            // Determine person request type
            if (urlPath.equals("/person/")) {
                response = personService.persons(authToken);
                persons = true;
            }
            else {
                String personID = urlPath.substring(PERSON_ID_START);
                response = personService.person(personID, authToken);
            }

            // Determine status of response
            if (response.getClass() != ErrorResponse.class) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream responseBody = httpExchange.getResponseBody();

                if (persons)
                    writeResponse(gson.toJson(((PersonsResponse) response).getPersons()), responseBody);
                else
                    writeResponse(gson.toJson((PersonResponse) response), responseBody);
                responseBody.close();
            }
            else {
            	// User error
                if (((ErrorResponse) response).getErrorType().equals("user")) {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }
                // Server error
                else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
                }
                OutputStream responseBody = httpExchange.getResponseBody();
                writeResponse(gson.toJson(((ErrorResponse) response).getMessage()), responseBody);
                responseBody.close();
            }
        }

        logger.exiting("PersonHandler", "handle");
    }
}
