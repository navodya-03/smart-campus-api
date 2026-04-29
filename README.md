# Smart Campus API (Tomcat + JAX-RS)

## Student Project Purpose
This project implements a RESTful Smart Campus API for managing campus rooms, sensors, and historical sensor readings. It is specifically designed to meet the coursework requirements by exclusively using:
* Java (JAX-RS with Jersey)
* Apache Tomcat deployment
* In-memory collections (ConcurrentHashMap, ArrayList) to ensure zero database dependency.

## Technology Stack
* Framework: JAX-RS, Jersey
* Server: Apache Tomcat 9
* Data Storage: Thread-safe In-memory Collections
* Response Format: JSON
* API Observability: java.util.logging via JAX-RS Container Filters

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
```

## How to Build & Run (IntelliJ IDEA)
1. Clone this repository to your local machine.
2. Open the project in IntelliJ IDEA.
3. Ensure Apache Tomcat is configured in your IDE (Add Configuration -> Smart Tomcat).
4. Set the Context Path to /smart-campus-api-tomcat and point the deployment directory to src/main/webapp.
5. Run the server. 
Base URL: http://localhost:8080/smart-campus-api-tomcat/api/v1

---

## Sample cURL Commands

1. Discovery Endpoint
curl -X GET http://localhost:8080/smart-campus-api-tomcat/api/v1

2. Create a Room
curl -X POST http://localhost:8080/smart-campus-api-tomcat/api/v1/rooms -H "Content-Type: application/json" -d '{"id": "LAB-101", "name": "Computer Lab", "capacity": 40}'

3. Get All Rooms
curl -X GET http://localhost:8080/smart-campus-api-tomcat/api/v1/rooms

4. Create a Sensor (Linked to Room)
curl -X POST http://localhost:8080/smart-campus-api-tomcat/api/v1/sensors -H "Content-Type: application/json" -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "LAB-101"}'

5. Filter Sensors by Type
curl -X GET "http://localhost:8080/smart-campus-api-tomcat/api/v1/sensors?type=Temperature"

6. Add a Sensor Reading
curl -X POST http://localhost:8080/smart-campus-api-tomcat/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d '{"value": 25.5}'

## Expected Error Handling
* 422 Unprocessable Entity: Returned when attempting to create a sensor with a roomId that does not exist in the system.
* 409 Conflict: Returned when attempting to delete a room that still has active sensors linked to it.
* 403 Forbidden: Returned when attempting to post a reading to a sensor that is currently in "MAINTENANCE" status.
* 500 Internal Server Error: A global safety net that catches unexpected runtime errors without leaking internal Java stack traces.

---

## Conceptual Report Answers

Part 1: Service Architecture & Setup
* JAX-RS Resource Lifecycle: By default, JAX-RS Resource classes are request-scoped, meaning a new instance is instantiated for every incoming HTTP request. To prevent data loss and manage state, the underlying data structures must be abstracted into a centralized Singleton class. To prevent race conditions in this concurrent environment, thread-safe collections such as ConcurrentHashMap are strictly required.
* HATEOAS: Hypermedia allows clients to dynamically discover API capabilities through embedded navigation links rather than relying on static documentation. This decouples the client from the server's routing structure, allowing the API to evolve without breaking client implementations.

Part 2: Room Management
* IDs vs Full Objects: Returning only IDs saves network bandwidth and reduces server-side serialization overhead, but forces the client to make subsequent requests to gather details. Returning full objects increases payload size but allows the client to render UI components immediately with a single request. 
* DELETE Idempotency: The DELETE operation is idempotent. If a client deletes a room, the first request successfully removes it. If the exact same request is sent again, the system state remains unchanged (the room is still gone), and it safely returns a 404 Not Found without causing any server-side exceptions.

Part 3: Sensor Operations & Linking
* @Consumes Mismatch: If a client attempts to send data in a different format (e.g., text/plain to an APPLICATION_JSON endpoint), JAX-RS intercepts the request before it reaches the resource method and automatically returns an HTTP 415 Unsupported Media Type error.
* QueryParam vs PathParam: Path parameters define the unique hierarchical identity of a specific resource. Query parameters are structurally superior for filtering because they are optional, composable, and do not fundamentally alter the underlying identity of the collection being accessed.

Part 4: Deep Nesting with Sub-Resources
* Sub-Resource Locator: Delegating logic to separate sub-resource classes (like SensorReadingResource) prevents massive, monolithic controller classes. It adheres to the Single Responsibility Principle, making the codebase modular, easier to maintain, and simpler to test.

Part 5: Advanced Error Handling & Logging
* 422 vs 404: HTTP 404 implies the requested URI endpoint does not exist. HTTP 422 Unprocessable Entity is semantically accurate for missing references because the endpoint and JSON syntax are perfectly valid, but the business logic (the referenced Room ID) fails validation.
* Security Risk of Stack Traces: Exposing internal Java stack traces reveals internal system architecture, file paths, and specific library versions. Attackers can use this fingerprinting data to identify known vulnerabilities and craft targeted exploits.
* JAX-RS Filters: Implementing filters applies Aspect-Oriented Programming (AOP). It centralizes cross-cutting concerns, ensuring that logging is consistently and automatically applied to all current and future endpoints without requiring developers to manually duplicate Logger.info() boilerplate in every method.
