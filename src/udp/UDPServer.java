package udp;

import common.KeyValueStore; // Import the KeyValueStore class
import common.Logger; // Import the Logger class
import java.net.DatagramPacket; // Import class to represent datagram packets
import java.net.DatagramSocket; // Import class for sending and receiving UDP datagram packets
import java.net.InetAddress;  // Import class for representing IP addresses
import java.net.SocketException; // Import for handling socket related exceptions.
import java.nio.charset.StandardCharsets; // Import for working with character encoding

/**
 * Represents a simple UDP server that handles key-value store operation requests.
 * This server listens on a specified port and manages a single-threaded key-value store,
 * including handling malformed requests.
 */
public class UDPServer {
    private KeyValueStore store; // The key-value store to manage data

     /**
     * Constructor for the UDPServer.
     *
     * @param port The port number the server will listen on for incoming UDP datagram packets.
     */
    public UDPServer(int port) {
        store = new KeyValueStore(); // Initialize key-value store.
        try (DatagramSocket socket = new DatagramSocket(port)) {  // Create a UDP socket, use try-with-resources to automatically close it.
            Logger.log("UDP Server started on port " + port); // Log that the server is started.
            byte[] buffer = new byte[1024]; // Create a buffer for receiving datagram packets.
            while (true) { // Infinite loop to always listen for requests
               try{
                     // Create a datagram packet to receive data.
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Wait until a packet is received.

                    InetAddress clientAddress = packet.getAddress(); // Get the client's IP address
                    int clientPort = packet.getPort(); // Get the client's port number
                    int packetLength = packet.getLength(); // Get the length of the received packet

                     // Try to decode the request as a String. If this fails, it means it's a malformed packet.
                    String request;
                    try {
                        request = new String(packet.getData(), 0, packetLength, StandardCharsets.UTF_8);

                    }
                    // Handle malformed packets: The request is not a valid UTF-8 string.
                    catch (IllegalArgumentException e){
                        Logger.log("Received malformed request of length "+ packetLength +" from " + clientAddress.getHostAddress() + ":" + clientPort); //Log the received malformed packet and its length.
                         String response = "ERROR: Malformed Request"; // Send response to indicate a malformed request.
                         byte[] responseBytes = response.getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort); //Create the response packet.
                        socket.send(responsePacket);  //Send the response back to client.
                        continue; // Skip to next iteration of the loop.

                     }

                    // Log and process the request if it's not malformed.
                    Logger.log("Received request from " + clientAddress.getHostAddress() + ":" + clientPort + ": " + request); //Log the received request.
                    String response = processRequest(request); // Process the request and get a response
                    byte[] responseBytes = response.getBytes(); // convert the response to bytes.
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort); // create a response packet using the response bytes, address and port.
                    socket.send(responsePacket);  // Send the response to the client.
                    Logger.log("Sent response to " + clientAddress.getHostAddress() + ":" + clientPort + ": " + response); // Log the sent response

                }
                // Log any other exceptions
                 catch(Exception e){
                   Logger.log("Error processing packet: " + e.getMessage()); //Log any error when processing packets
               }
            }
         } catch (SocketException e) {
            Logger.log("Server exception: " + e.getMessage()); // log any socket exceptions.
        }
        catch (Exception e) {
            Logger.log("Server exception: " + e.getMessage()); // log any general exceptions.
        }
    }

     /**
     * Processes the client's request and returns the appropriate response.
     *
     * @param request The request String from the client.
     * @return The response string to be sent back to the client.
     */
    private String processRequest(String request) {
        String[] parts = request.split(" "); // Split the request based on spaces.
        if (parts.length < 2) { // Check that there are at least two parts (operation and key).
            return "ERROR: Invalid request format"; // If not, return invalid format error message
        }

        String operation = parts[0];  // Get the operation from the first part.
        String key = parts[1]; // Get the key from the second part.
        String value = parts.length > 2 ? parts[2] : null; // Get the value if there is one.

         // Process the request based on the operation.
        switch (operation) {
            case "PUT":
                 // Return error if the value is missing
                if (value == null) {
                    return "ERROR: Missing value for PUT";
                }
                 // Put operation to add or update a value in the store.
                store.put(key, value);
                return "OK";
            case "GET":
               // Get the value for the specified key.
                String result = store.get(key);
               // If key exists, return its value; otherwise, return "NOT FOUND".
                return result != null ? result : "NOT FOUND";
            case "DELETE":
                // Removes the specified key from the store.
                store.delete(key);
                return "OK";
            default:
                return "ERROR: Unknown operation"; // if the operation is not supported, send an error.
        }
    }


    /**
     * Main method for the UDPServer.
     *
     * @param args Command line arguments, expecting one argument: the port number.
     */
    public static void main(String[] args) {
        if (args.length < 1) { // Checks that the correct number of command line arguments was given.
            System.out.println("Usage: java UDPServer <port>"); 
            return; 
        }
        int port = Integer.parseInt(args[0]); // Get the port from args and convert to int.
        new UDPServer(port); // Creates and starts the UDP server, with the specific port.
    }
}