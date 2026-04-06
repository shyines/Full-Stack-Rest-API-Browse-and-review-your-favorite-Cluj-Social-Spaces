# PeerReviewdsClujPubs

A student peer-review web application designed for finding and reviewing locations in Cluj-Napoca (entertainment, education, food).

## Core Use Cases
- **Students:** Browse local businesses, read reviews, submit ratings/comments, and vote on other reviews.
- **Owners:** Manage business listings, view analytics, and respond to student feedback.
- **Administrators:** Moderate content, manage users, and ensure platform integrity.
- **Guests:** Search and browse places without requiring an account.

## Server Architecture
The backend is built with **Spring Boot 3** and follows a standard N-tier architecture:

### 1. Model (Entity)
- **Role:** Defines the data structure and database schema.
- **Implementation:** JPA Entities (e.g., `User`, `Place`, `Review`) with annotations for table mapping, relationships (One-to-Many, Many-to-Many), and validation.

### 2. Repository
- **Role:** Handles direct database communication.
- **Implementation:** Spring Data JPA interfaces that provide CRUD operations and custom JPQL queries without boilerplate code.

### 3. Service
- **Role:** Orchestrates business logic and maintains transactional integrity.
- **Implementation:** Java classes that validate requests, perform complex calculations (e.g., average ratings), and manage security-sensitive operations.

### 4. Controller
- **Role:** Exposes the REST API and handles HTTP protocol concerns.
- **Implementation:** `@RestController` classes that map request paths, handle status codes, and convert Entities to **DTOs (Data Transfer Objects)** for clean JSON responses.

---

## Tech Stack
- **Backend:** Java 17, Spring Boot, Spring Data JPA, PostgreSQL.
- **Frontend:** Vanilla HTML5, CSS3, JavaScript (MVC Pattern).
- **Deployment:** Docker & Docker Compose.
