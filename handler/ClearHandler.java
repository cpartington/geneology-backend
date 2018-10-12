package handler;

import com.google.gson.*;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import response.Response;
import service.ClearService;

public class ClearHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new Gson();
        ClearService clearService = new ClearService();
        Response response;

        logger.entering("ClearHandler", "handle");

        // Execute clear
        response = clearService.clear();

        // Determine status of response
        if (response.isSuccess()) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(gson.toJson(response.getSuccessResponse()), responseBody);
            responseBody.close();
        }
        else {
            // User error
            if (response.getErrorType().equals("user")) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
            // Server error
            else {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            }
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(gson.toJson(response.getErrorResponse()), responseBody);
            responseBody.close();
        }

        logger.exiting("ClearHandler", "handle");
    }
}
