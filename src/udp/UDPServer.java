package udp;

import common.KeyValueStore;
import common.Logger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private KeyValueStore store;

    public UDPServer(int port) {
        store = new KeyValueStore();
        try (DatagramSocket socket = new DatagramSocket(port)) {
            Logger.log("UDP Server started on port " + port);
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    InetAddress clientAddress = packet.getAddress();
                    int clientPort = packet.getPort();
                    int packetLength = packet.getLength();

                    String request;
                    try {
                        request = new String(packet.getData(), 0, packetLength, StandardCharsets.UTF_8);
                    } catch (IllegalArgumentException e) {
                        Logger.log("Received malformed request of length " + packetLength + " from " +
                                clientAddress.getHostAddress() + ":" + clientPort);
                        String errorResponse = "ERROR: Malformed Request";
                        byte[] responseBytes = errorResponse.getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length,
                                clientAddress, clientPort);
                        socket.send(responsePacket);
                        continue;
                    }

                    Logger.log("Received request from " + clientAddress.getHostAddress() + ":" + clientPort + ": " + request);
                    String response = processRequest(request);
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length,
                            clientAddress, clientPort);
                    socket.send(responsePacket);
                    Logger.log("Sent response to " + clientAddress.getHostAddress() + ":" + clientPort + ": " + response);
                } catch(Exception e) {
                    Logger.log("Error processing packet: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            Logger.log("Server exception: " + e.getMessage());
        } catch (Exception e) {
            Logger.log("Server exception: " + e.getMessage());
        }
    }

    /**
     * Process client requests, requiring the request format to be 
     * "id operation key [value]".
     */
    private String processRequest(String request) {
        String[] parts = request.split(" ");
        String reqId = "0";
        if (parts.length < 2) {
            return reqId + " ERROR: Invalid request format";
        } else {
            reqId = parts[0];
        }
        if (parts.length < 3) {
            return reqId + " ERROR: Missing operation or key";
        }
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
        return reqId + " " + response;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java UDPServer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new UDPServer(port);
    }
}
