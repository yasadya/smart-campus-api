# Smart Campus Sensor & Room Management API

**Author:** yasadya  
**GitHub:** [yasadya/smart-campus-api](https://github.com/yasadya/smart-campus-api)  
**Module:** 5COSC022W – Client-Server Architectures  
**Version:** 1.0  
**Contact:** admin@smartcampus.ac.uk

---

## Overview

This is a RESTful API built with **JAX-RS (Jersey)** and an embedded **Grizzly HTTP server** for the University of Westminster's "Smart Campus" initiative. The API manages campus **Rooms** and **IoT Sensors** (e.g., CO2 monitors, temperature sensors, occupancy trackers), and maintains a historical log of **Sensor Readings**.

### Key Features

- Room creation, retrieval, and safe deletion (with sensor occupancy checks)
- Sensor registration with room-linkage validation
- Sensor filtering by type via query parameters
- Nested sub-resource for sensor reading history
- Full exception mapping (409, 422, 403, 500)
- Request/response logging via JAX-RS filters
- HATEOAS discovery endpoint

### Resource Hierarchy

```
/api/v1
├── /rooms
│   ├── GET    - List all rooms
│   ├── POST   - Create a room
│   └── /{roomId}
│       ├── GET    - Get room details
│       └── DELETE - Delete room (only if no sensors assigned)
└── /sensors
    ├── GET    - List all sensors (optional ?type= filter)
    ├── POST   - Register a sensor (validates roomId exists)
    └── /{sensorId}/readings
        ├── GET  - Get reading history
        └── POST - Add a new reading (updates sensor's currentValue)
```

---

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java 11+ |
| Framework | JAX-RS (Jersey 3.x) |
| Server | Grizzly HTTP (embedded) |
| Build Tool | Maven |
| Data Store | In-memory (`ConcurrentHashMap`) |
| JSON | Jackson (via Jersey media) |

---

## Build & Run Instructions

### Prerequisites

- Java 11 or higher installed
- Maven 3.6+ installed
- Terminal / Command Prompt

### Step 1 – Clone the repository

```bash
git clone https://github.com/yasadya/smart-campus-api.git
cd smart-campus-api
```

### Step 2 – Build the project

```bash
mvn clean package -DskipTests
```

This compiles all source files and packages them into a single executable JAR at `target/smart-campus-api-1.0.jar`.

### Step 3 – Run the server

```bash
java -jar target/smart-campus-api-1.0.jar
```

You should see:

```
===========================================
Smart Campus API started!
URL: http://localhost:8080/api/v1
===========================================
Press ENTER to stop...
```

The API is now live at: **http://localhost:8080/api/v1**

### Step 4 – Stop the server

Press **ENTER** in the terminal window where the server is running.

---

## Sample curl Commands

### 1. Discovery Endpoint – GET /api/v1

```bash
curl -X GET http://localhost:8080/api/v1
```

**Expected Response (200 OK):**
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

---

### 2. Create a Room – POST /api/v1/rooms

```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LIB-301","name":"Library Quiet Study","capacity":40}'
```

**Expected Response (201 Created):**
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

---

### 3. Register a Sensor – POST /api/v1/sensors

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":0.0,"roomId":"LIB-301"}'
```

**Expected Response (201 Created):**
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

---

### 4. Post a Sensor Reading – POST /api/v1/sensors/TEMP-001/readings

```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":22.5}'
```

**Expected Response (201 Created):**
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

---

### 5. Filter Sensors by Type – GET /api/v1/sensors?type=Temperature

```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"
```

**Expected Response (200 OK):**
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

---

### 6. Delete a Room with Sensors (409 Conflict Demo)

```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

**Expected Response (409 Conflict):**
```json
{
  "error": "ROOM_NOT_EMPTY",
  "message": "Room LIB-301 cannot be deleted because it still has sensors assigned to it.",
  "roomId": "LIB-301",
  "hint": "Remove or reassign all sensors from this room before deleting it."
}
```

---

### 7. Register Sensor with Invalid Room (422 Demo)

```bash
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"CO2-999","type":"CO2","status":"ACTIVE","currentValue":0.0,"roomId":"FAKE-999"}'
```

**Expected Response (422 Unprocessable Entity):**
```json
{
  "error": "UNPROCESSABLE_ENTITY",
  "message": "The referenced room with ID 'FAKE-999' does not exist in the system.",
  "referencedResourceId": "FAKE-999",
  "hint": "Ensure the roomId exists before registering a sensor."
}
```

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

## Conceptual Report – Question Answers

### Part 1 – Service Architecture & Setup

#### Q1: JAX-RS Resource Lifecycle – Per-Request vs Singleton

By default, JAX-RS instantiates a **new instance** of every resource class for each incoming HTTP request. This is known as the **per-request lifecycle**. The benefit is that each request gets its own isolated object, so there is no risk of one request's state leaking into another — ideal for stateless REST APIs.

However, this means resource classes themselves **cannot** be used to store shared data. If you stored rooms or sensors as instance fields inside `RoomResource`, each request would start with an empty map and data would be lost immediately after the response.

To solve this, the `DataStore` class in this project uses the **Singleton pattern** — a single static instance shared across all requests. It uses `ConcurrentHashMap` instead of a plain `HashMap` to prevent race conditions. A `ConcurrentHashMap` allows multiple threads to read and write concurrently without locking the entire map, ensuring data integrity when multiple API calls happen simultaneously without causing `ConcurrentModificationException` or data corruption.

---

#### Q2: HATEOAS – Hypermedia as the Engine of Application State

HATEOAS is the practice of including **navigation links** inside API responses so that clients can discover available actions dynamically rather than hardcoding URLs. For example, the discovery endpoint at `GET /api/v1` returns links to `/api/v1/rooms` and `/api/v1/sensors`.

This benefits client developers in several ways. First, clients do not need to memorise or hardcode every endpoint URL — they can start at the root and follow links. Second, if the API evolves and URLs change, clients that follow links rather than hardcoded paths are less likely to break. Third, it makes the API self-documenting at runtime, reducing dependence on external documentation. HATEOAS is considered a hallmark of mature REST design (Level 3 of the Richardson Maturity Model) because it gives the API the ability to guide clients through its own state transitions.

---

### Part 2 – Room Management

#### Q1: Returning Full Objects vs IDs in List Responses

When `GET /api/v1/rooms` returns a list, there are two options: return only IDs, or return full room objects.

Returning **only IDs** reduces the initial payload size but forces the client to make N additional requests (one per room) to retrieve details — this is known as the N+1 problem and dramatically increases latency and server load for large datasets.

Returning **full objects** increases the size of a single response but eliminates the need for follow-up requests. For most use cases — such as displaying a list of rooms in a dashboard — the client needs the name and capacity anyway, so returning full objects is far more efficient in practice. The tradeoff only tips toward IDs when objects are extremely large or when the client rarely needs detail for all items at once.

---

#### Q2: Is DELETE Idempotent in This Implementation?

Yes, the `DELETE /{roomId}` implementation is **idempotent**. Idempotency means that making the same request multiple times produces the same server-side outcome as making it once.

In this implementation: the first `DELETE` on an existing room removes it and returns `200 OK`. A second identical `DELETE` on the same room finds no room in the store and returns `404 Not Found`. The server state is identical after both calls — the room is gone. The HTTP status code differs (200 vs 404), but the **state of the resource** is unchanged by the second call, satisfying the definition of idempotency. This is consistent with the HTTP specification, which requires DELETE to be idempotent but does not mandate that repeated calls return the same status code.

---

### Part 3 – Sensor Operations & Linking

#### Q1: Consequences of @Consumes Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` annotation tells JAX-RS that the POST endpoint only accepts requests with a `Content-Type: application/json` header. If a client sends data as `text/plain` or `application/xml`, JAX-RS will automatically reject the request before it even reaches the method body and return **HTTP 415 Unsupported Media Type**. The resource method is never invoked. This is a built-in content negotiation mechanism that protects the API from receiving data it cannot deserialise, without requiring any manual checks inside the method.

---

#### Q2: @QueryParam vs Path Segment for Filtering

Using `@QueryParam` (e.g., `GET /api/v1/sensors?type=CO2`) is considered superior to embedding the filter in the path (e.g., `/api/v1/sensors/type/CO2`) for several reasons.

Query parameters are **optional by nature** — omitting `?type=` returns all sensors, while the path-based approach requires a completely separate route for the unfiltered case. Query parameters also scale naturally when multiple filters are added (e.g., `?type=CO2&status=ACTIVE`), whereas path-based filtering becomes deeply nested and hard to read. Additionally, REST convention treats path segments as **resource identifiers** and query parameters as **modifiers or filters** on a collection. Filtering is a modifier, not an identifier, so the query parameter approach correctly reflects the semantics of the operation.

---

### Part 4 – Deep Nesting with Sub-Resources

#### Q1: Architectural Benefits of the Sub-Resource Locator Pattern

The Sub-Resource Locator pattern delegates a nested path to a separate class. In this project, `SensorResource` handles `/api/v1/sensors` and returns a `SensorReadingResource` instance to handle `/api/v1/sensors/{sensorId}/readings`.

This approach has several architectural advantages. First, it enforces the **Single Responsibility Principle** — each class manages one resource type, making the code easier to read, test, and maintain. Second, it avoids creating a single massive controller class with dozens of methods, which would become unmanageable as the API grows. Third, it allows the sub-resource class to receive context (in this case the `sensorId`) through its constructor, keeping the code clean without relying on path parameter injection in deeply nested routes. In large APIs with many nested resources, this pattern is essential for keeping the codebase modular and navigable.

---

### Part 5 – Advanced Error Handling, Exception Mapping & Logging

#### Q1: Why HTTP 422 is More Accurate Than 404 for Missing References

When a client posts a new sensor with a `roomId` that does not exist, the request itself is syntactically valid JSON and the endpoint URL is correct — so `404 Not Found` would be misleading because nothing about the request URL is missing. The issue is a **semantic validation failure** inside the payload: the data references a resource that does not exist.

HTTP 422 Unprocessable Entity was designed precisely for this scenario — the server understands the request format but cannot process it due to semantic errors in the content. Using 422 communicates clearly to the client that the problem is with the data they submitted (specifically the `roomId` value), not with the endpoint or the HTTP method. This distinction helps client developers diagnose and fix the issue faster.

---

#### Q2: Security Risks of Exposing Java Stack Traces

Exposing raw Java stack traces to external API consumers is a significant cybersecurity risk for several reasons.

Stack traces reveal the **internal package structure and class names** of the application (e.g., `com.smartcampus.resource.RoomResource`), which helps attackers map the codebase. They expose the **exact line numbers** where errors occur, making it easier to target specific vulnerabilities. They can reveal **library names and versions** (e.g., Jersey 3.1.2, Jackson 2.15), allowing attackers to look up known CVEs for those exact versions. They may also leak **database query fragments, file paths, or configuration details** embedded in exception messages. The `GlobalExceptionMapper` in this project prevents all of this by catching every `Throwable`, logging the full detail server-side for developers, and returning only a generic `500 Internal Server Error` message to the client — containing no internal implementation details.

---

#### Q3: Why Filters are Superior to Per-Method Logging

Using a JAX-RS filter that implements both `ContainerRequestFilter` and `ContainerResponseFilter` to handle logging is far better than inserting `Logger.info()` calls into every resource method for several reasons.

Logging is a **cross-cutting concern** — it applies uniformly to every endpoint regardless of business logic. Filters enforce this uniformity automatically; if a new endpoint is added, it is logged without any additional code. Per-method logging, by contrast, requires every developer to remember to add log statements, leading to inconsistent coverage and duplicated boilerplate. Filters also capture information that per-method logging cannot easily access, such as the HTTP status code of the outgoing response. Finally, centralising logging in one place means it can be modified, enhanced, or disabled in a single class rather than hunting through every resource file. This is the principle of **separation of concerns** applied in practice.

---

## License

This project was developed as academic coursework for the University of Westminster (5COSC022W, 2025/26). All rights reserved.
