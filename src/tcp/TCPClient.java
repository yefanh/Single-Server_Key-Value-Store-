package tcp;

import common.Logger;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPClient {

    private static int requestIdCounter = 1;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TCPClient <hostname> <port>");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        // define 5 key-value pairs
        String[] keys = {"key1", "key2", "key3", "key4", "key5"};
        String[] values = {"value1", "value2", "value3", "value4", "value5"};

        // execute 5 PUT operations
        for (int i = 0; i < keys.length; i++) {
            String response = sendTCPRequest("PUT", keys[i], values[i], hostname, port);
            Logger.log("PUT Response: " + response);
        }

        // excute 5 GET operations
        for (int i = 0; i < keys.length; i++) {
            String response = sendTCPRequest("GET", keys[i], null, hostname, port);
            Logger.log("GET Response: " + response);
        }

        // execute 5 DELETE operations
        for (int i = 0; i < keys.length; i++) {
            String response = sendTCPRequest("DELETE", keys[i], null, hostname, port);
            Logger.log("DELETE Response: " + response);
        }
    }

    private static String sendTCPRequest(String operation, String key, String value, String hostname, int port) {
        int requestId = requestIdCounter++;
        // construct the request string
        String request;
        if (value != null) {
            request = requestId + " " + operation + " " + key + " " + value;
        } else if (key != null) {
            request = requestId + " " + operation + " " + key;
        } else {
            request = requestId + " " + operation;
        }

        try (Socket socket = new Socket(hostname, port)) {
            socket.setSoTimeout(5000); // set a timeout for the socket
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Logger.log("Sent request: " + request);
            out.println(request);

            String responseLine = in.readLine();
            if (responseLine == null) {
                Logger.log("No response received for request id " + requestId);
                return "No response";
            }
            Logger.log("Received response: " + responseLine);

            // check if the response contains the request id
            String[] tokens = responseLine.split(" ", 2);
            if (tokens.length < 2) {
                Logger.log("Malformed response: " + responseLine);
                return responseLine;
            }
            int responseId = Integer.parseInt(tokens[0]);
            if (responseId != requestId) {
                Logger.log("Unsolicited response: expected id " + requestId + " but got " + responseId);
            }
            return tokens[1]; // return the response message
        } catch (SocketTimeoutException e) {
            Logger.log("Timeout waiting for response for request id " + requestId);
            return "Timeout";
        } catch (IOException e) {
            Logger.log("Client exception: " + e.getMessage());
            return "Error";
        }
    }
}
