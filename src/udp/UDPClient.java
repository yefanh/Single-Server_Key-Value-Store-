package udp;

import common.Logger; // Import the Logger class from the common package
import java.net.DatagramPacket; // Import class to represent datagram packets
import java.net.DatagramSocket; // Import class for sending and receiving UDP datagram packets
import java.net.InetAddress; // Import class for representing IP addresses

/**
 * Represents a simple UDP client that sends key-value store operation requests to a server.
 * It constructs the requests and handles responses over UDP.
 */
public class UDPClient {
    /**
     * Main method for the UDPClient.
     *
     * @param args Command line arguments, expecting: <hostname> <port> <operation> [key] [value]
     */
    public static void main(String[] args) {
        if (args.length < 3) { // Validates that the correct number of command line arguments were passed in.
            System.out.println("Usage: java UDPClient <hostname> <port> <operation> [key] [value]"); // If not correct, print the correct usage message.
            return; // exit the application
        }
        String hostname = args[0]; // Get host name from argument
        int port = Integer.parseInt(args[1]); // Get the port from the arguments, and convert to an int.
        String operation = args[2]; // Get the operation
        String key = args.length > 3 ? args[3] : null; // Get the key if there is one.
        String value = args.length > 4 ? args[4] : null; // Get the value if there is one.

        try (DatagramSocket socket = new DatagramSocket()) { // Create a UDP socket with try-with-resources to automatically close the socket.
            InetAddress address = InetAddress.getByName(hostname);  // Get the IP address for host.

            // Construct the request
             String request = operation + " " + (key != null ? key : "") + " " + (value != null ? value : ""); // Create the request string
            byte[] buffer = request.getBytes(); // Convert the request string into a byte array.
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port); // Create the UDP datagram packet with the request bytes, address and port.

             // Send the request
            Logger.log("Sent request to " + hostname + ":" + port + ": " + request); //Log the sent request
            socket.send(packet); // Send the datagram packet to the server.

             // Receive the response
            byte[] responseBuffer = new byte[1024]; // Initialize a response buffer.
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length); // create the response packet.
            socket.receive(responsePacket); // Wait until you receive a response from the server.
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength()); // Convert the received response byte array to a string.
            Logger.log("Received response: " + response);  
        } catch (Exception e) {
            Logger.log("Client exception: " + e.getMessage()); //Log any exceptions that occur while processing the packet.
        }
    }
}