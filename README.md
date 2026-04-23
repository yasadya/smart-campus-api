# Smart Campus Sensor & Room Management API

![Java](https://img.shields.io/badge/Java-11%2B-007396?style=flat-square&logo=java&logoColor=white)
![JAX-RS](https://img.shields.io/badge/JAX--RS-Jersey%203.x-6DB33F?style=flat-square)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-Academic%20Use%20Only-lightgrey?style=flat-square)

---

> **Module:** 5COSC022W – Client-Server Architectures (2025/26)  
> **Institution:** Informatics Institute of Technology, in collaboration with the University of Westminster  
> **Module Leader:** Mr. Hamed Hamzeh  
> **Author:** R.P.Y.Lakdini &nbsp;|&nbsp; IIT ID: `20242101` &nbsp;|&nbsp; UOW ID: `w2153609`

---

## Overview

This is a RESTful API built with **JAX-RS (Jersey)** and an embedded **Grizzly HTTP server**, developed for the University of Westminster's *Smart Campus* initiative. The API provides a structured backend for managing campus **Rooms** and **IoT Sensors** — including CO₂ monitors, temperature sensors, and occupancy trackers — and maintains a persistent historical log of **Sensor Readings**.

### Key Features

| Feature | Description |
|---|---|
| Room Management | Create, retrieve, and safely delete rooms with occupancy validation |
| Sensor Registration | Register sensors with room-linkage validation |
| Sensor Filtering | Filter sensors by type via query parameters |
| Reading History | Nested sub-resource for sensor reading logs |
| Exception Mapping | Structured error responses: `409`, `422`, `403`, `500` |
| Request Logging | Centralised logging via JAX-RS filters |
| HATEOAS | Root discovery endpoint for self-documenting API navigation |

---

## Resource Hierarchy

```
/api/v1
├── /rooms
│   ├── GET         List all rooms
│   ├── POST        Create a new room
│   └── /{roomId}
│       ├── GET     Retrieve room details
│       └── DELETE  Delete room (blocked if sensors are assigned)
└── /sensors
    ├── GET         List all sensors (supports ?type= filter)
    ├── POST        Register a new sensor (validates roomId)
    └── /{sensorId}/readings
        ├── GET     Retrieve reading history
        └── POST    Record a new reading (updates sensor's currentValue)
```

---

## Technology Stack

| Component     | Technology                      |
|---------------|---------------------------------|
| Language      | Java 11+                        |
| Framework     | JAX-RS (Jersey 3.x)             |
| Server        | Grizzly HTTP (embedded)         |
| Build Tool    | Apache Maven                    |
| Data Store    | In-memory (`ConcurrentHashMap`) |
| Serialisation | Jackson (via Jersey media)      |

---

## Build & Run Instructions

### Prerequisites

- Java 11 or higher
- Apache Maven 3.6+
- Terminal / Command Prompt

### Step 1 — Clone the Repository

```bash
git clone https://github.com/yasadya/smart-campus-api.git
cd smart-campus-api
```

### Step 2 — Build the Project

```bash
mvn clean package -DskipTests
```

This compiles all source files and produces a single executable JAR at `target/smart-campus-api-1.0.jar`.

### Step 3 — Start the Server

```bash
java -jar target/smart-campus-api-1.0.jar
```

On successful startup, the console will display:

```
===========================================
Smart Campus API started!
URL: http://localhost:8080/api/v1
===========================================
Press ENTER to stop...
```

The API will be accessible at: **`http://localhost:8080/api/v1`**

### Step 4 — Stop the Server

Press **ENTER** in the terminal window where the server is running.

---

## API Reference & Sample Requests

### Discovery Endpoint

```http
GET /api/v1
```

```bash
curl -X GET http://localhost:8080/api/v1
```

<details>
<summary>Response — 200 OK</summary>

```json
{
  "name": "Smart Campus Sensor & Room Management API",
  "version": "1.0",
  "contact": "admin@smartcampus.ac.uk",
  "description": "RESTful API for managing campus rooms and IoT sensors",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```
</details>

---

### Create a Room

```http
POST /api/v1/rooms
Content-Type: application/json
```

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":40}'
```

<details>
<summary>Response — 201 Created</summary>

```json
{
  "message": "Room created successfully",
  "room": {
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 40,
    "sensorIds": []
  }
}
```
</details>

---

### Register a Sensor

```http
POST /api/v1/sensors
Content-Type: application/json
```

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

<details>
<summary>Response — 201 Created</summary>

```json
{
  "message": "Sensor registered successfully",
  "sensor": {
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
  }
}
```
</details>

---

### Record a Sensor Reading

```http
POST /api/v1/sensors/{sensorId}/readings
Content-Type: application/json
```

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":22.5}'
```

<details>
<summary>Response — 201 Created</summary>

```json
{
  "message": "Reading recorded successfully",
  "reading": {
    "id": "a1b2c3d4-...",
    "timestamp": 1714000000000,
    "value": 22.5
  },
  "updatedSensorValue": 22.5
}
```
</details>

---

### Filter Sensors by Type

```http
GET /api/v1/sensors?type=Temperature
```

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

<details>
<summary>Response — 200 OK</summary>

```json
[
  {
    "id": "TEMP-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 22.5,
    "roomId": "LIB-301"
  }
]
```
</details>

---

### Error Scenarios

#### 409 Conflict — Deleting a Room with Active Sensors

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

<details>
<summary>Response — 409 Conflict</summary>

```json
{
  "error": "ROOM_NOT_EMPTY",
  "message": "Room LIB-301 cannot be deleted because it still has sensors assigned to it.",
  "roomId": "LIB-301",
  "hint": "Remove or reassign all sensors from this room before deleting it."
}
```
</details>

#### 422 Unprocessable Entity — Sensor References a Non-Existent Room

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-999","type":"CO2","status":"ACTIVE","currentValue":0.0,"roomId":"FAKE-999"}'
```

<details>
<summary>Response — 422 Unprocessable Entity</summary>

```json
{
  "error": "UNPROCESSABLE_ENTITY",
  "message": "The referenced room with ID 'FAKE-999' does not exist in the system.",
  "referencedResourceId": "FAKE-999",
  "hint": "Ensure the roomId exists before registering a sensor."
}
```
</details>

---

## Project Structure

```
smart-campus-api/
├── pom.xml
└── src/main/java/com/smartcampus/
    ├── Main.java
    ├── SmartCampusApplication.java
    ├── model/
    │   ├── Room.java
    │   ├── Sensor.java
    │   └── SensorReading.java
    ├── store/
    │   └── DataStore.java
    ├── resource/
    │   ├── DiscoveryResource.java
    │   ├── RoomResource.java
    │   ├── SensorResource.java
    │   └── SensorReadingResource.java
    ├── exception/
    │   ├── RoomNotEmptyException.java
    │   ├── LinkedResourceNotFoundException.java
    │   ├── SensorUnavailableException.java
    │   └── mapper/
    │       ├── RoomNotEmptyExceptionMapper.java
    │       ├── LinkedResourceNotFoundExceptionMapper.java
    │       ├── SensorUnavailableExceptionMapper.java
    │       └── GlobalExceptionMapper.java
    └── filter/
        └── LoggingFilter.java
```

---

## Conceptual Report

<details>
<summary><strong>Part 1 — Service Architecture & Setup</strong></summary>

<br>

**Q1: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance created per request or is it a singleton? How does this affect in-memory data synchronisation?**

By default, JAX-RS creates a new instance of every resource class for each incoming HTTP request (per-request lifecycle). This means resource classes are stateless from one request to the next, which is ideal for REST APIs — no risk of one request's data leaking into another. However, this also means shared data cannot be stored as instance fields inside resource classes, because each new instance would start fresh and any data written would be lost when the request ends. To solve this, the DataStore class in this project uses the Singleton pattern — a single static instance shared across all requests and all resource class instances. It uses ConcurrentHashMap instead of a plain HashMap to handle thread safety. The use of ConcurrentHashMap ensures thread-safe access without requiring explicit synchronization, allowing multiple requests to be processed efficiently in parallel.

ConcurrentHashMap allows multiple threads to read and write concurrently without locking the entire structure, preventing race conditions and ConcurrentModificationException that could corrupt data when multiple API requests are processed simultaneously.

Additionally, JAX-RS supports alternative lifecycles such as singleton resources using the `@Singleton` annotation, but the default request-scoped lifecycle is preferred as it avoids shared mutable state and simplifies concurrency management.

---

**Q2: Why is HATEOAS considered a hallmark of advanced RESTful design? How does it benefit client developers?**

HATEOAS (Hypermedia as the Engine of Application State) is the practice of embedding navigation links inside API responses so clients can discover available actions dynamically, rather than relying on hardcoded URLs. For example, the discovery endpoint at `GET /api/v1` returns links to `/api/v1/rooms` and `/api/v1/sensors`. This benefits client developers in several ways: clients do not need to memorise every endpoint URL — they can start at the root and follow links; if the API evolves and URLs change, clients that follow links are less likely to break than those with hardcoded paths; and the API becomes self-documenting at runtime, reducing dependence on external documentation. HATEOAS represents Level 3 of the Richardson Maturity Model — the most mature form of REST design — because it allows the API to guide clients through its own state transitions.

This implementation aligns with Level 3 of the Richardson Maturity Model, representing the highest level of REST maturity where the API dynamically guides client interactions.

</details>

---

<details>
<summary><strong>Part 2 — Room Management</strong></summary>

<br>

**Q1: When returning a list of rooms, what are the implications of returning only IDs versus returning full room objects?**

Returning only IDs reduces the initial payload size but forces the client to make N additional requests (one per room) to retrieve details — known as the N+1 problem. This dramatically increases latency and server load for large datasets and makes the client logic more complex. Returning full objects increases the size of a single response but eliminates the need for follow-up requests. For most use cases — such as displaying a room list in a dashboard — the client needs the name and capacity anyway, so returning full objects is far more efficient. The tradeoff only favours returning IDs when objects are extremely large or when the client rarely needs detail for all items at once.

In this implementation, returning full room objects was chosen as the optimal design because the Room model is lightweight and clients typically require multiple attributes (such as name and capacity) for display purposes.

---

**Q2: Is the DELETE operation idempotent in your implementation? Justify with what happens on repeated calls.**

Yes, the `DELETE /{roomId}` implementation is idempotent. Idempotency means making the same request multiple times produces the same server-side outcome as making it once. In this implementation, the first DELETE on an existing room removes it and returns `200 OK`. A second identical DELETE on the same room finds nothing in the store and returns `404 Not Found`. The server state is identical after both calls — the room is gone regardless. The HTTP status code differs (200 vs 404), but the state of the resource is unchanged by the second call, satisfying the definition of idempotency. This is consistent with the HTTP specification, which requires DELETE to be idempotent but does not mandate that repeated calls return the same status code.

This behaviour ensures compliance with HTTP semantics, where idempotency is defined by the effect on server state rather than identical response codes.

</details>

---

<details>
<summary><strong>Part 3 — Sensor Operations & Linking</strong></summary>

<br>

**Q1: What are the technical consequences if a client sends data in `text/plain` or `application/xml` to a `@Consumes(APPLICATION_JSON)` endpoint?**

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the POST endpoint only accepts requests with a `Content-Type: application/json` header. If a client sends data as `text/plain` or `application/xml`, JAX-RS automatically rejects the request before it reaches the method body and returns HTTP `415 Unsupported Media Type`. The resource method is never invoked. This is a built-in content negotiation mechanism that protects the API from receiving data it cannot deserialise, without requiring any manual checks inside the method itself.

This mechanism is part of HTTP content negotiation, allowing the server to strictly enforce supported media types and ensure reliable deserialization.

---

**Q2: Why is the `@QueryParam` approach generally considered superior to embedding the type in the URL path for filtering?**

Using `@QueryParam` (e.g., `GET /api/v1/sensors?type=CO2`) is superior to path-based filtering (e.g., `/api/v1/sensors/type/CO2`) for several reasons. Query parameters are optional by nature — omitting `?type=` returns all sensors, whereas path-based filtering requires a completely separate route for the unfiltered case. Query parameters also scale naturally when multiple filters are added (e.g., `?type=CO2&status=ACTIVE`), whereas path-based filtering becomes deeply nested and hard to read. REST convention treats path segments as resource identifiers and query parameters as modifiers or filters on a collection. Filtering is a modifier not an identifier — so the query parameter approach correctly reflects the semantics of the operation.

This design also improves API readability and aligns with widely accepted REST conventions used in industry-grade APIs.

</details>

---

<details>
<summary><strong>Part 4 — Deep Nesting with Sub-Resources</strong></summary>

<br>

**Q1: Discuss the architectural benefits of the Sub-Resource Locator pattern.**

The Sub-Resource Locator pattern delegates a nested path to a dedicated class. In this project, `SensorResource` handles `/api/v1/sensors` and returns a `SensorReadingResource` instance to handle `/api/v1/sensors/{sensorId}/readings`. This has several architectural advantages. First, it enforces the Single Responsibility Principle where each class manages one resource type, making code easier to read, test, and maintain. Second, it avoids a single massive controller class with dozens of methods that would become unmanageable as the API grows. Third, the sub-resource class can receive context (the `sensorId`) through its constructor, keeping the code clean without relying on deeply nested path parameter injection. In large APIs with many nested resources, this pattern is essential for keeping the codebase modular and navigable.

This approach mirrors real-world hierarchical relationships between resources, where sensor readings are naturally subordinate to a specific sensor.

</details>

---

<details>
<summary><strong>Part 5 — Advanced Error Handling, Exception Mapping & Logging</strong></summary>

<br>

**Q1: Why is HTTP 422 often considered more semantically accurate than 404 when a referenced resource inside a valid JSON payload does not exist?**

When a client posts a new sensor with a `roomId` that does not exist, the request itself is syntactically valid JSON and the endpoint URL is correct — so `404 Not Found` would be misleading because nothing about the request URL is missing. The issue is a semantic validation failure inside the payload: the data references a resource that does not exist. HTTP `422 Unprocessable Entity` was designed precisely for this scenario. The server understands the request format but cannot process it due to semantic errors in the content. Using 422 communicates clearly to the client that the problem is with the data they submitted (specifically the `roomId` value), not with the endpoint or HTTP method, helping client developers diagnose and fix the issue faster.

This improves API usability by providing more precise error semantics, enabling clients to handle different failure scenarios appropriately.

---

**Q2: From a cybersecurity standpoint, explain the risks of exposing internal Java stack traces to external API consumers.**

Exposing raw Java stack traces to external consumers is a significant security risk for several reasons. Stack traces reveal the internal package structure and class names (e.g., `com.smartcampus.resource.RoomResource`), helping attackers map the codebase. They expose exact line numbers where errors occur, making it easier to target specific vulnerabilities. They can reveal library names and versions (e.g., Jersey 3.1.2, Jackson 2.15), allowing attackers to look up known CVEs for those exact versions. They may also leak database query fragments, file paths, or configuration details embedded in exception messages. The `GlobalExceptionMapper` in this project prevents all of this by catching every `Throwable`, logging full detail server-side for developers, and returning only a generic `500 Internal Server Error` message to the client containing no internal implementation details.

This practice follows the principle of information hiding, which is critical in preventing attackers from gaining insights into the system architecture.

---

**Q3: Why is it advantageous to use JAX-RS filters for logging rather than manually inserting `Logger.info()` statements in every resource method?**

Using a JAX-RS filter implementing both `ContainerRequestFilter` and `ContainerResponseFilter` for logging is far better than per-method Logger calls for several reasons. Logging is a cross-cutting concern — it applies uniformly to every endpoint regardless of business logic. Filters enforce this uniformity automatically; if a new endpoint is added, it is logged without any additional code.

Per-method logging, by contrast, requires every developer to remember to add log statements, leading to inconsistent coverage and duplicated boilerplate. Filters also capture information that per-method logging cannot easily access, such as the HTTP status code of the outgoing response. Centralising logging in one place means it can be modified or disabled in a single class rather than editing every resource file. This is the principle of separation of concerns applied in practice.

This design improves maintainability and ensures consistent logging behaviour across the entire application without duplicating code.

</details>

---

## Additional Design Considerations

In addition to the core requirements, several design considerations were taken into account to improve the API's robustness and maintainability.

Full CRUD operations were implemented for the primary resources, ensuring complete lifecycle management aligned with RESTful principles.

Pagination support was also introduced for collection endpoints to improve scalability and prevent large datasets from overwhelming the client or server.

Although a DTO (Data Transfer Object) layer was not implemented in this version, it is recognised as a valuable enhancement for future development. Introducing DTOs would allow the API to decouple internal domain models from external representations, improving security by preventing exposure of internal structures and increasing flexibility when evolving the API.

Validation annotations such as `@NotNull` and `@Min` were also considered as an improvement to enforce stronger data integrity at the API boundary.

---

## License

This project was developed exclusively as academic coursework for the **University of Westminster** (Module: 5COSC022W, Academic Year 2025/26). All rights reserved.

&copy; R.P.Y.Lakdini — Informatics Institute of Technology
