# Minimal Blog API - README

## Overview

The Minimal Blog is a lightweight, modern blog API developed as a personal project to showcase my skills in back-end development, security, and database management. This project demonstrates proficiency in Spring Boot, RESTful API design, JWT authentication and database modeling.

## Technologies Used

- **Backend**: Spring Boot 3.4.5, Java 21
- **Database**: PostgreSQL, H2 (for testing)
- **Security**: Spring Security, JWT (using JJWT), Password Encoding
- **API Documentation**: OpenAPI (Swagger) 2.8.8
- **Mapping**: MapStruct
- **Email**: Spring Mail with MailTrap
- **Build Tool**: Maven
- **Libraries**: Lombok, Hibernate
- **Containerization**: Docker, Docker Compose

## Features

- User authentication and registration with JWT-based security.
- Password reset functionality via email.
- CRUD operations for blog posts, categories, and tags.
- Draft and published post management with role-based access (ADMIN/USER).
- API endpoints documented with Swagger UI for easy testing.
- Reading time calculation for posts and post count tracking for categories/tags.
- Transactional data handling with Hibernate/JPA.

## Project Structure

The application follows a clean architecture with:

- `controllers`: RESTful endpoints for authentication, posts, categories, and tags.
- `domain`: DTOs and entities defining the data model.
- `services`: Business logic implementation.
- `repositories`: JPA repositories for database operations.
- `security`: Custom JWT authentication and password reset services.
- `config`: Configuration for OpenAPI and Spring Security.
- `mappers`: MapStruct for entity-DTO mapping.

## Setup Instructions

To run the project locally:

1. **Install Prerequisites**:
    - Ensure Docker, Java 21, and Maven are installed.

2. **Start Docker Services**:
    - Navigate to the project directory.
    - Run `docker compose up --build` to start PostgreSQL and Adminer.
    - Verify services are running by accessing Adminer at `http://localhost:8888` (default credentials: server `db`, user `root`, password `secret`, database `minimal-blog`).

3. **Configure Environment**:
   - Copy `application-example.properties` to `application.properties` in the `src/main/resources` directory.
   - Update `application.properties` with your configuration:
      - Replace `your_jwt_secret_key_here` with a secure JWT secret key.
      - Update `your_mailtrap_username` and `your_mailtrap_password` with your MailTrap credentials.
      - Set `your_verified_email@example.com` to your verified email for sending password reset emails.
    - The PostgreSQL connection is handled by Docker Compose. You can access the database manually using adminer at  `http://localhost:8888`

4. **Build the Application**:
   - Execute `./mvnw clean package` to build the project and generate a JAR file (located in the `target` directory).
   - Run the built JAR with `java -jar target/minimal-blog-0.0.1-SNAPSHOT.jar`.
   - Access Swagger UI at `http://localhost:8080/swagger-ui.html`.
    
## Basic Usage

- Register a new user via `/api/v1/auth/register`.
- Log in via `/api/v1/auth/login` to obtain a JWT token.
- Use the token to access protected endpoints (e.g., `/api/v1/posts/drafts`).
- Reset password via `/api/v1/auth/forgot-password` and `/api/v1/auth/reset-password`.
