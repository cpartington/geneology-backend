package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import request.LoginRequest;
import response.AccessResponse;
import response.ErrorResponse;
import response.Response;
import service.LoginService;

public class LoginHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new Gson();

        logger.entering("LoginHandler", "handle");

        if (!httpExchange.getRequestMethod().toLowerCase().equals("post")) {
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(gson.toJson(new ErrorResponse("Invalid request body.", "user")), responseBody);
            responseBody.close();
        }
        else {
            // Get login request
            InputStream requestBody = httpExchange.getRequestBody();
            LoginRequest loginRequest = null;
            try {
                loginRequest = gson.fromJson(readRequest(requestBody), LoginRequest.class);
            }
            catch (JsonSyntaxException e) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                writeResponse(gson.toJson(new ErrorResponse("Invalid request body.", "user")), responseBody);
                responseBody.close();
            }

            // Login
            LoginService loginService = new LoginService();
            Response response = loginService.login(loginRequest);

            // Check for success
            if (response.getClass() == AccessResponse.class) {
                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream responseBody = httpExchange.getResponseBody();
                writeResponse(gson.toJson((AccessResponse) response), responseBody);
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

        logger.exiting("LoginHandler", "handle");
    }
}
