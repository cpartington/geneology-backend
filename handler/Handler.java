package handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class Handler {

    protected static Logger logger;

    static {
        logger = Logger.getLogger("familymapserver");
    }

    /**
     * Exceptions for bad user HTTP requests.
     */
    public static class BadRequestException extends Exception {

        public BadRequestException() {

        }

        public BadRequestException(String message) {
            super(message);
        }

    }

    /**
     * Exceptions for server errors while executing HTTP requests.
     */
    public static class ServerErrorException extends Exception {

        public ServerErrorException() {

        }

        public ServerErrorException(String message) {
            super(message);
        }

    }

    /**
     * Writes a response to the output stream.
     * @param message a JSON string
     * @param responseBody the HTTP exchange response body
     * @throws IOException
     */
    public void writeResponse(String message, OutputStream responseBody) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(responseBody);
        writer.write(message);
        writer.flush();
    }

    public String readRequest(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

}
