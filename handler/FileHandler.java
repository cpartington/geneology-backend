package handler;

import com.google.gson.*;
import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;

import service.Service;

public class FileHandler extends Handler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        try {
            if (httpExchange.getRequestMethod().toLowerCase().equals("get")) {
                String urlPath = httpExchange.getRequestURI().getPath();
                // Get index file
                File file = null;
                String webPath = "C:\\Users\\chris\\AndroidStudioProjects\\FamilyMap\\web\\";
                // Get correct file
                if (urlPath.equals("/index.html") || urlPath.equals("/")) {
                    // Send the webpage
                    file = new File(webPath + "index.html");
                }
                else {
                    String pathElements[] = urlPath.split("/");
                    String filepath = new String();
                    for (int i = 1; i < pathElements.length; i++)  {
                        filepath += pathElements[i];
                        if (i < pathElements.length - 1) {
                            filepath += File.separator;
                        }
                    }
                    file = new File(webPath + filepath);
                }
                // Send back files
                if (file.exists() && file.canRead()) {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream responseBody = httpExchange.getResponseBody();
                    Files.copy(file.toPath(), responseBody);
                    responseBody.close();
                }
                else {
                    file = new File(webPath + "HTML" + File.separator + "404.html");
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream responseBody = httpExchange.getResponseBody();
                    Files.copy(file.toPath(), responseBody);
                    responseBody.close();
                }

                // Make sure database exists
                Service service = new Service();
                service.createTables();
            }
            else
                throw new BadRequestException("Invalid request.");
        }
        catch (BadRequestException e) {
            Gson gson = new Gson();
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            OutputStream responseBody = httpExchange.getResponseBody();
            writeResponse(e.getMessage(), responseBody);
            responseBody.close();
        }
    }
}
