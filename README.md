# Minimal Blog API

A RESTful API built with Spring Boot and Java, demonstrating core backend development skills. This project provides a secure, role-based content management system for a blog application.

## Key Features

* **Authentication & Authorization:** Secure JWT-based authentication with refresh tokens and role-based access control (Admin, Editor, User).
* **Content Management:** Full CRUD operations for posts, categories, and tags, supporting draft/published workflows.
* **Email Service:** Implemented for password reset and author notifications for reviewed posts.
* **API Documentation:** Interactive API documentation available via Swagger UI.
* **Database Management:** PostgreSQL integration for structured data storage.

## Technologies

* **Backend:** Spring Boot 3.4.5, Java 21, Spring Security 6
* **Database:** PostgreSQL, Hibernate/JPA
* **Security:** JWT (JJWT), BCrypt
* **API Tools:** OpenAPI 3 / Swagger UI, MapStruct
* **Deployment:** Docker, Maven

## Project Structure

* `controllers`: Defines RESTful endpoints.
* `domain`: Contains DTOs and entities.
* `services`: Implements business logic.
* `repositories`: Manages database operations.
* `security`: Handles JWT and password reset logic.
* `config`: Handles configuration for OpenAPI and Spring Security.

## Getting Started (Local)

1.  **Prerequisites:** Ensure Java 21+, Docker, and Maven are installed.
2.  **Clone Repository:** `git clone https://github.com/2016mehrab/minimal-blog-api.git && cd minimal-blog-api`
3.  **Configuration:** Customize `application.properties` in `src/main/resources/` for local application settings. Refer to `application-example.properties` for guidance.
4.  **Run with Docker Compose:**
    ```bash
    docker compose up --build
    ```
    * **Access Swagger UI:** `http://localhost:8080/swagger-ui.html`
    * **Access Adminer (DB GUI):** `http://localhost:8888`
        * *(Server: `db`, User: `root`, Pass: `secret`, DB: `minimal-blog`)*

## Deployment

* **Backend Hosting:** Railway.app (Free Tier)
* **Database Hosting:** Neon (PostgreSQL, Free Tier)
* **Frontend Hosting:** [Netlify](https://minimal-blog-mehrab.netlify.app/home)

*For more information about the frontend, please visit the main [Minimal Blog Frontend repository](https://github.com/2016mehrab/minimal-blog).*