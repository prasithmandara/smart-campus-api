# Smart Campus Sensor & Room Management API

## Overview
This is a RESTful API built with JAX-RS (Jersey) for the 5COSC022W 
Client-Server Architectures coursework. It manages Rooms and Sensors 
across a Smart Campus using in-memory ConcurrentHashMaps.

## How to Build and Run

### Prerequisites
- Java JDK 11 or 17
- Apache Maven

### Steps
1. Clone the repository
2. Run: mvn clean package
3. Run: mvn tomcat7:run
4. Server starts at: http://localhost:8080

## Sample curl Commands

### 1. Discover API
curl -X GET http://localhost:8080/smart-campus-api/api/v1

### 2. Get all rooms
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms

### 3. Create a room
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"LAB-105","name":"Computer Lab E","capacity":30}'

### 4. Get sensors filtered by type
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"

### 5. Post a sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":23.4}'

### 6. Delete room with sensors (409 error)
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301

### 7. Create sensor with invalid roomId (422 error)
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id":"BAD-001","type":"CO2","status":"ACTIVE","currentValue":0,"roomId":"FAKE-999"}'

## Report Answers

### Part 1.1 - JAX-RS Resource Lifecycle
As per the default setting in JAX-RS, there is a new object instantiation for every resource class for every HTTP request (request scope). 
Hence, all information contained within the object will be lost after every HTTP request. All information must be contained within a Singleton DataStore class, 
which will only instantiate once and will be used throughout the entire process. 
The use of ConcurrentHashMap is instead of HashMap since HTTP requests may occur at the same time on different threads.

### Part 1.2 - HATEOAS
In other words, HATEOAS is when navigation links are included within the API responses for discovering actions without needing to refer to external documentation. 
For instance, when using GET /api/v1, one would be able to access links for /api/v1/rooms and /api/v1/sensors. 
This allows the API to become self-documenting since client programmers can use the entire API by simply starting at the root path. 
Another advantage of HATEOAS is low coupling, where the URLs may be modified at any time without changing anything for clients discovering them.

### Part 2.1 - IDs vs Full Objects
Returning IDs only means that the client will need to send an extra HTTP request per room to get the room’s information. 
This creates what is known as the N+1 problem, leading to much higher latency and server load. 
While returning full objects will incur higher bandwidth costs, it is done with just one HTTP request. 
For a large dataset of thousands of rooms, there won’t be much difference in bandwidth consumption compared to making N+1 requests. 
For a huge number of records, pagination is the right way to go. 

### Part 2.2 - DELETE Idempotency
Yes, DELETE is an idempotent method in this scenario. 
Idempotency implies sending the same request several times will have the same effect as sending it just one time. 
When the DELETE request is sent the first time, the room is deleted (204 No Content). 
When the DELETE request is sent the second time, the room is already deleted and will return a 404 Not Found response. 
Regardless of how many times the DELETE request is sent, the server state will always be the same; that is, there will be no room at all.

### Part 3.1 - @Consumes Mismatch
The annotation @Consumes(MediaType.APPLICATION_JSON) indicates that the method consumes only the requests with Content-Type header as application/json. 
When the client makes an incoming request using either text/plain or application/xml, then JAX-RS matches the request’s Content-Type header with the 
@Consumes annotation. Since there will be no match found, JAX-RS automatically generates the response HTTP 415 Unsupported Media Type. 
No coding is required here, and everything is done by JAX-RS internally.

### Part 3.2 - QueryParam vs PathParam
The use of query parameters such as ?type=CO2 is the proper REST implementation when filtering a collection. 
The path /sensors represents the resource, while the query parameter refines the results obtained. 
The use of query parameters is optional – their absence means that all sensors will be returned. 
Path parameters (/sensors/type/CO2) would imply that ‘type’ is a sub-resource, which is incorrect, as well as conflicting with the path /sensors/{sensorId}.

### Part 4.1 - Sub-Resource Locator Pattern
In the Sub-Resource Locator pattern, the processing of the request of the nested URI is passed to some other class. 
The SensorResource class contains a method annotated by @Path('{sensorId}/readings') which returns the SensorReadingResource class, 
hence now JAX-RS will take care of that class to handle the request. 
Pros: The classes will be responsible for one thing only; reading will be tested separately from the sensors; 
there will be no difficulty in adding additional nested URIs because it will not make the parent class obese.
When there are many nested URIs in complex APIs, they end up making one gigantic class called god classes.

### Part 5.2 - HTTP 422 vs 404
The error code 404 Not Found occurs when the resource requested by the URL cannot be found. In this case, however, the URL /api/v1/sensors is totally valid and working just fine. 
It's the contents of the request body that causes problems; the roomId parameter refers to a non-existent room. 
Error code 422 Unprocessable Entity is just what is needed for such a situation, where a request looks correct in syntax but is semantically incorrect due to a non-existent entity.

### Part 5.4 - Stack Trace Security Risks
Displaying stack trace poses a significant security threat because it shows: 
(1) library names and version numbers, which allow an attacker to search for a corresponding CVE 
(2) internal names of packages and classes, indicating the overall architecture of the program
(3) absolute server paths, which help with directory traversal
(4) line numbers and program execution order, helping reverse engineer business logic and look for potential exploits
(5) database driver names and SQL code if the query fails, making SQL injection more effective.
The GlobalExceptionMapper logs all information on the server side but only sends a general message back to the client.

### Part 5.5 - Filters vs Manual Logging
Placing Logger.info() within each resource class method is against the principle of DRY (Don’t Repeat Yourself), 
mixes infrastructure code with business logic, lacks consistency (one can easily overlook adding such code when implementing new methods), 
and forces us to modify all classes to switch the logging library. The JAX-RS filter is invoked on every request and response made without fail, 
ensuring total consistency, eliminating duplicated code, and maintaining clean separation of concerns. Logging, authentication, 
and CORS fall under cross-cutting concerns that should be handled by filters.
