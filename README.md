
# Spring Security with JWT Authentication 🔐

![Java](https://img.shields.io/badge/Java-17%2B-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1%2B-brightgreen)
![Security](https://img.shields.io/badge/Security-JWT-orange)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)

A secure REST API implementation using Spring Security with JSON Web Token (JWT) authentication. This project demonstrates modern security best practices for protecting Spring Boot applications.

## 📌 Features

- **JWT-based authentication** (Access Token)
- Role-based authorization (`USER`, `ADMIN`)
- Secure password storage with BCrypt
- Custom security filters
- Exception handling for security scenarios
- Ready-to-use authentication endpoints

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3.1+**
- Spring Security
- JJWT (Java JWT Library)
- Lombok
- H2 Database (for demonstration)

## 🍵 JWT Structure

```mermaid
graph LR
    A[JWT Structure] --> B[Header]
    A --> C[Payload]
    A --> D[Signature]
    B -->|alg:HS256| E[Algorithm]
    C --> F[sub:Username]
    C --> G[iat:Timestamp]
    C --> H[exp:Expiration]
```


## 🔐 Security Flow

```mermaid
sequenceDiagram
    participant Client
    participant Server
    participant Database
    
    Client->>Server: POST /auth/login (credentials)
    Server->>Database: Verify credentials
    Database-->>Server: User details
    Server->>Server: Generate JWT
    Server-->>Client: Return JWT
    
    Client->>Server: Request with JWT (Authorization header)
    Server->>Server: Validate JWT
    Server->>Database: Check user permissions
    Database-->>Server: User roles
    Server-->>Client: Return authorized data
```

