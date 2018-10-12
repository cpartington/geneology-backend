package main;

import com.sun.net.httpserver.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.*;

import handler.ClearHandler;
import handler.EventHandler;
import handler.FillHandler;
import handler.LoadHandler;
import handler.LoginHandler;
import handler.PersonHandler;
import handler.RegisterHandler;

/**
 * Acts as
 */
public class Server {

    // STATIC MEMBERS

    private static final int MAX_WAITING_CONNECTIONS = 12;

    private static Logger logger;

    static {
        try {
            initLog();
        }
        catch (IOException e) {
            System.out.println("Could not initialize log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initializes the logger.
     * @throws IOException if something goes wrong
     */
    private static void initLog() throws IOException {

        Level logLevel = Level.FINEST;

        logger = Logger.getLogger("familymapserver");
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(logLevel);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);

        FileHandler fileHandler = new FileHandler("log.txt", false);
        fileHandler.setLevel(logLevel);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
    }

    // NON-STATIC MEMBERS

    /** Receives and responds to HTTP requests. */
    private HttpServer server;

    /**
     * Initializes and runs the server.
     *
     * @param portNumber the port on which the server should accept client connections
     */
    private void run(String portNumber) {
        logger.info("Initializing HTTP Server");

        // Initialize the server.
        try {
            server = HttpServer.create(new InetSocketAddress(Integer.parseInt(portNumber)),
                                       MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Use the default executor.
        server.setExecutor(null);

        logger.info("Creating contexts");

        // Create HTTP handlers for each possible URL path.
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/", new handler.FileHandler());

        logger.info("Starting server");
        server.start();
        logger.info("Server started.");
    }

    /**
     * Runs the server.
     *
     * @param args command-line arguments
     */
    public static void main(String args[]) {
        String portNumber = args[0];
        new Server().run(portNumber);
    }

}
