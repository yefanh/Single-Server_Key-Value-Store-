# Key-Value Store (TCP & UDP)

## **Project Overview**
This project implements a single-server **Key-Value Store** that supports **TCP and UDP** communication protocols. It allows clients to perform:
- **PUT (key, value)** - Store a key-value pair.
- **GET (key)** - Retrieve the value associated with a key.
- **DELETE (key)** - Remove a key from the store.

The project supports **timeout mechanisms** and **client-side automatic prepopulation of data**, fulfilling the assignment requirements.

---

## **Setup and Running the Project**

### **1. Compilation**
Run the following command in the project root directory to compile all Java files:
```bash
javac common/*.java tcp/*.java udp/*.java
```

### **2. Running the Server**
#### **Start the TCP Server (Port 8080)**
```bash
java tcp.TCPServer 8080
```
#### **Start the UDP Server (Port 9090)**
```bash
java udp.UDPServer 9090
```

### **3. Running the Client**
#### **Run the TCP Client**
```bash
java tcp.TCPClient localhost 8080
```
#### **Run the UDP Client**
```bash
java udp.UDPClient localhost 9090
```

---

## **Testing and Validation**
Upon execution, the client will automatically perform:
- **5 PUT operations**
- **5 GET operations**
- **5 DELETE operations**

### **Example Logs**
```txt
[2025-02-28 17:22:34.940] Sent request: 1 PUT key1 value1
[2025-02-28 17:22:34.963] Received response: 1 OK
...
[2025-02-28 17:22:56.280] Sent request to localhost:9090: 15 DELETE key5
[2025-02-28 17:22:56.281] Received response: 15 OK
```

The server will log incoming requests, responses, and errors if any.

---

## **Error Handling and Timeout Mechanism**

### **Implemented Features:**
- **Timeout Mechanism:** If the server does not respond within **5 seconds**, the client logs a timeout error and proceeds with the next request.
- **Malformed Request Handling:** The server rejects and logs malformed requests with appropriate error messages.
- **Resilience to Server Failure:** If the server crashes, the client logs the error and moves forward without hanging.

### **Common Issues & Solutions**
| **Issue** | **Cause** | **Solution** |
|----------|---------|-------------|
| **Server Crashes** | Unexpected error or termination | Restart the server and rerun the client |
| **Port Already in Use** | Another process is using the port | Choose a different port and restart the server |
| **UDP Requests Timeout** | Server is down or unreachable | Ensure the UDP server is running and reachable |

---

## **Authors and Contributors**
- **Name:** Yefan He
- **Date:** February 28, 2025

This project meets all assignment requirements and is ready for submission.

