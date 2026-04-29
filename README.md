# Smart Campus API (Tomcat + JAX-RS)

## Student Project Purpose
This project implements a RESTful Smart Campus API for managing campus rooms, sensors, and historical sensor readings. It is specifically designed to meet the coursework requirements by exclusively using:
* Java (JAX-RS with Jersey)
* Apache Tomcat deployment
* In-memory collections (ConcurrentHashMap, ArrayList) to ensure zero database dependency.

## Technology Stack
* **Framework:** JAX-RS, Jersey
* **Server:** Apache Tomcat 9
* **Data Storage:** Thread-safe In-memory Collections
* **Response Format:** JSON
* **API Observability:** `java.util.logging` via JAX-RS Container Filters

## Project Structure
```text
smart-campus-api/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/smartcampus/
        │   ├── SmartCampusApplication.java
        │   ├── Room.java
        │   ├── Sensor.java
        │   ├── SensorReading.java
        │   ├── DataStore.java
        │   ├── RoomResource.java
        │   ├── SensorResource.java
        │   ├── SensorReadingResource.java
        │   ├── LoggingFilter.java
        │   └── GlobalExceptionMapper.java
        └── webapp/WEB-INF/web.xml

How to Build & Run (IntelliJ IDEA)

Clone this repository to your local machine.
Open the project in IntelliJ IDEA.
Ensure Apache Tomcat is configured in your IDE (Add Configuration -> Smart Tomcat).
Set the Context Path to /smart-campus-api-tomcat and point the deployment directory to src/main/webapp.
Run the server.
Base URL: http://localhost:8080/smart-campus-api-tomcat/api/v1

Sample cURL Commands

1. Discovery Endpoint
curl -X GET http://localhost:8080/smart-campus-api-tomcat/api/v1

2. Create a Room
curl -X POST http://localhost:8080/smart-campus-api-tomcat/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "LAB-101", "name": "Computer Lab", "capacity": 40}'

3. Get All Rooms
curl -X GET http://localhost:8080/smart-campus-api-tomcat/api/v1/rooms

4.Create a Sensor (Linked to Room)
curl -X POST http://localhost:8080/smart-campus-api-tomcat/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "LAB-101"}'

5. Filter Sensors by Type
curl -X GET "http://localhost:8080/smart-campus-api-tomcat/api/v1/sensors?type=Temperature"

6. Add a Sensor Reading
curl -X POST http://localhost:8080/smart-campus-api-tomcat/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 25.5}'

Expected Error Handling

422 Unprocessable Entity: Returned when attempting to create a sensor with a roomId that does not exist in the system.
409 Conflict: Returned when attempting to delete a room that still has active sensors linked to it.
403 Forbidden: Returned when attempting to post a reading to a sensor that is currently in "MAINTENANCE" status.
500 Internal Server Error: A global safety net that catches unexpected runtime errors without leaking internal Java stack traces.
