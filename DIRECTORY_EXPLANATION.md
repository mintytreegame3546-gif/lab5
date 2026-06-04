# Codebase Explanation

This repository implements a Java client-server command-processing system using UDP networking, object serialization, and the Command design pattern.

The repository already contains a detailed explanation in `PROGRAM_EXPLANATION.txt`. Key topics covered include:

- Client-server architecture
- UDP networking with DatagramSocket and DatagramChannel
- Serialization and deserialization of Java objects
- Command pattern implementation
- Collection management and server-side command execution
- Database configuration and authentication helpers
- Blocking vs non-blocking I/O
- Java networking concepts and design patterns

## Main Directory Structure

### src/client
Client-side application entry point and request handling.

### src/server
Server entry point, request processing, response sending, authentication, database access, and command execution.

### src/server/commands
Concrete command implementations executed by the server.

### src/network
Shared network DTOs, serialization utilities, request and response objects.

### src/data
Domain model classes such as Organization and Address.

### src/managers
Collection management and business logic.

## Control Flow

1. Client reads a command.
2. Command is wrapped in a request object.
3. Request is serialized.
4. Request is sent over UDP.
5. Server receives request.
6. Server deserializes request.
7. ServerCommandProcessor dispatches the command.
8. Command executes business logic.
9. Response object is created.
10. Response is serialized and sent back.
11. Client deserializes and displays the response.

See PROGRAM_EXPLANATION.txt for the detailed file-by-file and topic-by-topic explanation.
