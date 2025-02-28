package tcp;

import common.KeyValueStore;
import common.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
    private KeyValueStore store;

    public TCPServer(int port) {
        store = new KeyValueStore();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Logger.log("TCP Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            Logger.log("Server exception: " + e.getMessage());
        }
    }

    /**
     * deal with the client request
     */
    private void handleClient(Socket clientSocket) {
        String clientInfo = clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine();
            if (request == null) {
                Logger.log("Received empty request from " + clientInfo);
                return;
            }
            Logger.log("Received request from " + clientInfo + ": " + request);

            String[] parts = request.split(" ");
            if (parts.length < 2) {
                String errorResponse = "ERROR: Invalid request format";
                out.println("0 " + errorResponse);
                Logger.log("Sent response to " + clientInfo + ": " + errorResponse);
                return;
            }
            // parts[0] is the request ID
            String reqId = parts[0];
            String operation = parts[1];
            String response = "";
            switch (operation) {
                case "PUT":
                    if (parts.length < 4) {
                        response = "ERROR: Missing key or value for PUT";
                    } else {
                        String key = parts[2];
                        StringBuilder valueBuilder = new StringBuilder();
                        for (int i = 3; i < parts.length; i++) {
                            valueBuilder.append(parts[i]).append(" ");
                        }
                        String value = valueBuilder.toString().trim();
                        store.put(key, value);
                        response = "OK";
                    }
                    break;
                case "GET":
                    if (parts.length != 3) {
                        response = "ERROR: Invalid GET request. Format: GET <key>";
                    } else {
                        String key = parts[2];
                        String value = store.get(key);
                        response = (value != null) ? value : "NOT FOUND";
                    }
                    break;
                case "DELETE":
                    if (parts.length != 3) {
                        response = "ERROR: Invalid DELETE request. Format: DELETE <key>";
                    } else {
                        String key = parts[2];
                        store.delete(key);
                        response = "OK";
                    }
                    break;
                default:
                    response = "ERROR: Unknown operation";
            }
            String fullResponse = reqId + " " + response;
            out.println(fullResponse);
            Logger.log("Sent response to " + clientInfo + ": " + fullResponse);
        } catch (IOException e) {
            Logger.log("Client handling exception for " + clientInfo + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java TCPServer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new TCPServer(port);
    }
}
