package handler;

import com.google.gson.*;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import response.ErrorResponse;
import response.EventResponse;
import response.EventsResponse;
import response.Response;
import service.EventService;

public class EventHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final int EVENT_ID_START = 7;
        boolean events = false;
        Gson gson = new Gson();
        Response response = null;

        logger.entering("EventHandler", "handle");

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
            EventService eventService = new EventService();

            // Determine event request type
            if (urlPath.equals("/event/")) {
                response = eventService.events(authToken);
                events = true;
            }
            else {
                String eventID = urlPath.substring(EVENT_ID_START);
                response = eventService.event(eventID, authToken);
            }

            // Determine status of response
            if (response.getClass() != ErrorResponse.class) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream responseBody = httpExchange.getResponseBody();

                if (events)
                    writeResponse(gson.toJson((EventsResponse) response), responseBody);
                else
                    writeResponse(gson.toJson((EventResponse) response), responseBody);
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
        logger.exiting("EventHandler", "handle");
    }


}
