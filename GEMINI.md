# Context: University Project - Student Peer-Review Web Application

I am developing a client-server application for a university project.
The goal is a peer-review site for students to find and review locations (entertainment, education, food).

## Tech Stack:
- **Backend:** Java 17+, Spring Boot 3, Spring Data JPA.
- **Database:** PostgreSQL (running in Docker).
- **Frontend:** Vanilla HTML, CSS, and JavaScript (Client-side MVC architecture).
- **Communication:** REST API (JSON).
- **Environment:** Linux (Pop!_OS), IntelliJ IDEA Ultimate.

## Current Project Structure:
proiect-student-review/
├── docker-compose.yml (Postgres config: port 5432, user: 'user', pass: '1234', db: 'student_review')
├── server/ (Spring Boot app)
│   ├── src/main/java/com/studentreview/server/
│   │   ├── model/User.java (Entity)
│   │   ├── config/WebConfig.java (CORS configuration)
│   │   └── ServerApplication.java
│   └── src/main/resources/application.properties
└── client/ (Frontend files)
├── index.html
├── style.css
└── app.js

## Implementation Rules:
1. **Language:** All code comments and documentation must be in English.
2. **Architecture:** Maintain a clear separation between layers (Controller, Service, Repository, Model).
3. **Frontend:** Do not use frameworks like React/Vue. Use Vanilla JS with MVC pattern.
4. **Database:** Use JPA 'update' mode for schema generation.

## Current State:
- Docker and DB connection are working.
- CORS is enabled.
- The 'User' entity is created.
- A simple 'Hello World' endpoint is working and tested with the frontend via 'fetch'.
