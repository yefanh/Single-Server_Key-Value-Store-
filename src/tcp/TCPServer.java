package tcp;

import common.KeyValueStore;
import common.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Represents a simple TCP server that handles key-value store operations.
 * This server listens on a specified port and manages a single-threaded key-value store.
 */
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
     * Handles a single client connection and processes the request.
     *
     * @param clientSocket The socket connected to the client.
     */
    private void handleClient(Socket clientSocket) {
      try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
           PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

          String request = in.readLine(); // Read the request from the client
          Logger.log("Received request: " + request); // Log the received request

          String[] parts = request.split(" ");  // Split the request into parts based on spaces.
          String response = "ERROR: Invalid request"; // Default response in case of invalid request

          switch (parts[0]) { // Switch based on the operation requested by the client
              case "PUT":
                  // Checks if at least key and value is sent
                  if (parts.length >= 3) {
                      // Combine all parts after the key into the value
                      StringBuilder valueBuilder = new StringBuilder();
                      for (int i = 2; i < parts.length; i++) {
                          valueBuilder.append(parts[i]).append(" ");
                      }
                      String value = valueBuilder.toString().trim(); // Convert StringBuilder to String and remove trailing spaces.
                      store.put(parts[1], value); // Call the put method in key-value store.
                      response = "OK"; // Sets the response as OK
                  }
                  break;
              case "GET":
                  if (parts.length == 2) { // Checks for 2 parts - "GET" and "key"
                      String value = store.get(parts[1]); // Calls the get method of the key-value store.
                       response = value != null ? value : "NOT FOUND"; // Sets the response from the returned value or "NOT FOUND"
                  }
                  break;
              case "DELETE":
                  if (parts.length == 2) { // Checks for 2 parts - "DELETE" and "key"
                      store.delete(parts[1]); // Removes key from key-value store.
                      response = "OK";  // Sets the response as OK.
                  }
                  break;
          }
          out.println(response);  // Send the response back to the client
          Logger.log("Sent response: " + response); // Log the sent response

      } catch (IOException e) {
          Logger.log("Client handling exception: " + e.getMessage()); // Log any exception that occurred during client handling.
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