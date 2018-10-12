package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;
import service.FillService;

public class FillHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final int DEFAULT_GENS = 4;
        final int USERNAME = 2;
        final int GENS = 3;
        final int NO_GEN_LENGTH = 3;
        final int YES_GEN_LENGTH = 4;
        Gson gson = new Gson();
        Response response = null;

        logger.entering("FillHandler", "handle");

        String urlPath = httpExchange.getRequestURI().getPath();
        FillService fillService = new FillService();

        // Determine fill request type
        String[] urlData = urlPath.split("/");

        if (urlData.length < NO_GEN_LENGTH || urlData.length > YES_GEN_LENGTH) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(gson.toJson(new ErrorResponse("Invalid fill request.", "user")), responseBody);
            responseBody.close();
        }
        else {
            if (urlData.length == NO_GEN_LENGTH) {
                response = fillService.fill(urlData[USERNAME], DEFAULT_GENS);
            }
            else {
                int generations = 0;
                try {
                    generations = Integer.parseInt(urlData[GENS]);
                }
                catch (NumberFormatException e) {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    OutputStream responseBody = httpExchange.getResponseBody();
                    writeResponse(gson.toJson(new ErrorResponse("Invalid generation parameter.", "user")), responseBody);
                    responseBody.close();
                }
                response = fillService.fill(urlData[USERNAME], generations);
            }

            if (response.getClass() == SuccessResponse.class) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                writeResponse(gson.toJson(((SuccessResponse) response).getMessage()), responseBody);
                responseBody.close();
            } else {
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
        logger.exiting("FillHandler", "handle");
    }
}
