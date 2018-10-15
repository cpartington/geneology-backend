package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import request.LoadRequest;
import response.ErrorResponse;
import response.Response;
import response.SuccessResponse;
import service.LoadService;

public class LoadHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new Gson();

        logger.entering("LoadHandler", "handle");

        if (!httpExchange.getRequestMethod().toLowerCase().equals("post")) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(gson.toJson(new ErrorResponse("Invalid request body.", "user")), responseBody);
            responseBody.close();
        }
        else {
            // Get load request
            InputStream requestBody = httpExchange.getRequestBody();
            LoadRequest loadRequest = null;
            try {
                loadRequest = gson.fromJson(readRequest(requestBody), LoadRequest.class);
            }
            catch (JsonSyntaxException e) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                writeResponse(gson.toJson(new ErrorResponse("Invalid request body.", "user")), responseBody);
                responseBody.close();
            }

            // Load data
            LoadService loadService = new LoadService();
            Response response = loadService.load(loadRequest);

            // Check for success
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
        logger.exiting("LoadHandler", "handler");
    }
}
