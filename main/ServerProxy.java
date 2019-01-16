package net;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import request.*;
import response.AccessResponse;
import response.ErrorResponse;
import response.EventsResponse;
import response.GenericResponse;
import response.PersonsResponse;

public class ServerProxy {

    private static ServerProxy _instance = new ServerProxy();

    public static ServerProxy getInstance() {
        return _instance;
    }

    private String hostName;
    private String portNumber;

    private ServerProxy() {

    }

    /**
     * Connects to the server to log in a user.
     *
     * @param loginRequest the data to log in
     */
    public GenericResponse login(LoginRequest loginRequest) {
        Gson gson = new Gson();

        try {
            // Create url
            URL url = new URL("http://" + hostName + ":" + portNumber + "/user/login");

            // Construct request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            // Send request
            http.connect();
            OutputStream requestBody = http.getOutputStream();
            writeRequest(gson.toJson(loginRequest), requestBody);
            requestBody.close();

            // Check response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = http.getInputStream();
                return gson.fromJson(readResponse(responseBody), AccessResponse.class);
            } else {
                InputStream responseBody = http.getErrorStream();
                return gson.fromJson(readResponse(responseBody), ErrorResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse("HTTP connection failed.");
        }
    }

    /**
     * Connects to the server in order to register a new user.
     *
     * @param registerRequest the data for registration
     */
    public GenericResponse register(RegisterRequest registerRequest) {
        Gson gson = new Gson();

        try {
            // Create url
            URL url = new URL("http://" + hostName + ":" + portNumber + "/user/register");

            // Construct request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            // Send request
            http.connect();
            OutputStream requestBody = http.getOutputStream();
            writeRequest(gson.toJson(registerRequest), requestBody);
            requestBody.close();

            // Check response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = http.getInputStream();
                return gson.fromJson(readResponse(responseBody), AccessResponse.class);
            } else {
                InputStream responseBody = http.getErrorStream();
                return gson.fromJson(readResponse(responseBody), ErrorResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse("HTTP connection failed.");
        }
    }

    /**
     * Connects to the server to obtain all persons associated with a user.
     *
     * @param authToken the auth token provided by the user
     */
    public GenericResponse person(String authToken) {
        Gson gson = new Gson();

        try {
            // Create url
            URL url = new URL("http://" + hostName + ":" + portNumber + "/person/");

            // Construct request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);

            // Send request
            http.connect();

            // Check response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = http.getInputStream();
                return gson.fromJson(readResponse(responseBody), PersonsResponse.class);
            } else {
                InputStream responseBody = http.getErrorStream();
                return gson.fromJson(readResponse(responseBody), ErrorResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse("HTTP connection failed.");
        }
    }

    /**
     * Connects to the server to obtain all events associated with a user.
     *
     * @param authToken auth token provided by the user
     */
    public GenericResponse event(String authToken) {
        Gson gson = new Gson();

        try {
            // Create url
            URL url = new URL("http://" + hostName + ":" + portNumber + "/event/");
            // add more for eventID??

            // Construct request
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);

            // Send request
            http.connect();

            // Check response
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream responseBody = http.getInputStream();
                return gson.fromJson(readResponse(responseBody), EventsResponse.class);
            } else {
                InputStream responseBody = http.getErrorStream();
                return gson.fromJson(readResponse(responseBody), ErrorResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ErrorResponse("HTTP connection failed.");
        }
    }

    /**
     * Writes a response to the output stream.
     *
     * @param message     a JSON string
     * @param requestBody the HTTP exchange response body
     * @throws IOException
     */
    private void writeRequest(String message, OutputStream requestBody) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(requestBody);
        writer.write(message);
        writer.flush();
    }

    /**
     * Reads a request from the input stream.
     *
     * @param is the input stream
     * @return the string version of the input
     * @throws IOException
     */
    private String readResponse(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPortNumber() {
        return portNumber;
    }
}
