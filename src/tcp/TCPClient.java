package tcp;

import common.Logger;
import java.io.*;
import java.net.Socket;

/**
 * The TCPClient class is a simple TCP client that connects to a server,
 * sends a request, and receives a response. The request and response
 * are logged using a Logger.
 *
 * Usage: java TCPClient <hostname> <port> <operation> [key] [value]
 *
 * <hostname> - The hostname of the server to connect to.
 * <port> - The port number of the server to connect to.
 * <operation> - The operation to perform (e.g., GET, PUT).
 * [key] - The key for the operation (optional, depending on the operation).
 * [value] - The value for the operation (optional, depending on the operation).
 *
 * Example:
 * java TCPClient localhost 8080 PUT myKey myValue
 *
 * The client sends a request in the format: "operation key value"
 * and logs the sent request and received response.
 *
 * If an IOException occurs, it is logged as a client exception.
 */
public class TCPClient {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java TCPClient <hostname> <port> <operation> [key] [value]");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        String operation = args[2];
        String key = args.length > 3 ? args[3] : null;
        String value = args.length > 4 ? args[4] : null;

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String request = operation + " " + (key != null ? key : "") + " " + (value != null ? value : "");
             Logger.log("Sent request: " + request); // Log the sent request
            out.println(request);
            String response = in.readLine();
            Logger.log("Received response: " + response);
        } catch (IOException e) {
            Logger.log("Client exception: " + e.getMessage());
        }
    }
}