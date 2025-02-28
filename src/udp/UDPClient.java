package udp;

import common.Logger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPClient {
    private static int requestIdCounter = 1;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java UDPClient <hostname> <port>");
            return;
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        // define 5 key-value pairs
        String[] keys = {"key1", "key2", "key3", "key4", "key5"};
        String[] values = {"value1", "value2", "value3", "value4", "value5"};

        // execute 5 PUT operations
        for (int i = 0; i < keys.length; i++) {
            String response = sendUDPRequest("PUT", keys[i], values[i], hostname, port);
            Logger.log("PUT Response: " + response);
        }

        // execute 5 GET operations
        for (int i = 0; i < keys.length; i++) {
            String response = sendUDPRequest("GET", keys[i], null, hostname, port);
            Logger.log("GET Response: " + response);
        }

        // execute 5 DELETE operations
        for (int i = 0; i < keys.length; i++) {
            String response = sendUDPRequest("DELETE", keys[i], null, hostname, port);
            Logger.log("DELETE Response: " + response);
        }
    }

    private static String sendUDPRequest(String operation, String key, String value, String hostname, int port) {
        int requestId = requestIdCounter++;
        String request;
        if (value != null) {
            request = requestId + " " + operation + " " + key + " " + value;
        } else if (key != null) {
            request = requestId + " " + operation + " " + key;
        } else {
            request = requestId + " " + operation;
        }

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress address = InetAddress.getByName(hostname);
            byte[] buffer = request.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            Logger.log("Sent request to " + hostname + ":" + port + ": " + request);
            socket.send(packet);

            byte[] responseBuffer = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            socket.receive(responsePacket);
            String responseLine = new String(responsePacket.getData(), 0, responsePacket.getLength());
            Logger.log("Received response: " + responseLine);

            // 检查响应中的请求 ID 是否匹配
            String[] tokens = responseLine.split(" ", 2);
            if (tokens.length < 2) {
                Logger.log("Malformed response: " + responseLine);
                return responseLine;
            }
            int responseId = Integer.parseInt(tokens[0]);
            if (responseId != requestId) {
                Logger.log("Unsolicited response: expected id " + requestId + " but got " + responseId);
            }
            return tokens[1];
        } catch (SocketTimeoutException e) {
            Logger.log("Timeout waiting for response for request id " + requestId);
            return "Timeout";
        } catch (Exception e) {
            Logger.log("Client exception: " + e.getMessage());
            return "Error";
        }
    }
}
