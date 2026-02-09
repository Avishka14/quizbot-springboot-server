# QuizBot Spring Boot Server - Project Documentation


---

## Project Overview

### Description
QuizBot is a comprehensive quiz generation and management system designed to facilitate the creation, distribution, and evaluation of quizzes. This repository contains the backend server built with Spring Boot, providing RESTful APIs for the React-based frontend application. quiz bot uses DeepSeek API for Quiz generation

### Purpose
The system aims to:
- Enable educators and content creators to generate quizzes efficiently
- Provide a robust platform for quiz management
- Offer real-time quiz taking and evaluation
- Track user performance and generate analytics
- Support multiple question types and difficulty levels

### Key Objectives
- **Scalability**: Handle multiple concurrent users and quiz sessions
- **Reliability**: Ensure data integrity and system availability
- **User Experience**: Provide fast response times and seamless integration with frontend
- **Security**: Protect user data and prevent unauthorized access

---

## System Architecture


```
┌─────────────────┐
│  React Frontend │
│   (Separate)    │
└────────┬────────┘
         │ HTTP/REST
         ▼
┌─────────────────────────────────┐
│   Spring Boot Application       │
│  ┌──────────────────────────┐  │
│  │   Controller Layer       │  │
│  │  (REST Endpoints)        │  │
│  └───────────┬──────────────┘  │
│              ▼                  │
│  ┌──────────────────────────┐  │
│  │   Service Layer          │  │
│  │  (Business Logic)        │  │
│  └───────────┬──────────────┘  │
│              ▼                  │
│  ┌──────────────────────────┐  │
│  │   Repository Layer       │  │
│  │  (Data Access)           │  │
│  └───────────┬──────────────┘  │
└──────────────┼──────────────────┘
               ▼
      ┌────────────────┐
      │  MySQL Database │
      └────────────────┘
```

### Design Patterns
- **MVC (Model-View-Controller)**: Separation of concerns
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer between layers
- **Dependency Injection**: Loose coupling and testability

---

## Technology Stack

### Backend Framework
- **Spring Boot** 2.7.x / 3.x
  - Spring Web (REST API)
  - Spring Data JPA (Database interaction)
  - Spring Security (Authentication & Authorization)
  - Spring Validation (Input validation)

### Database
- **MySQL** 8.0+
  - Relational database for structured data
  - ACID compliance for data integrity

### Build Tool
- **Maven** or **Gradle**
  - Dependency management
  - Build automation

### Additional Libraries
- **Lombok**: Reduce boilerplate code
- **ModelMapper**: Object mapping
- **Jackson**: JSON processing
- **Hibernate**: ORM implementation
- **JWT**: Token-based authentication
- **Swagger/SpringDoc**: API documentation

### Development Tools
- **Java** 11/17/21
- **IntelliJ IDEA / Eclipse / VS Code**
- **Postman**: API testing
- **MySQL Workbench**: Database management

### Ai Intergration 
- **DeepSeek v1** via OpenRouter free Version 
---

## Features

### Core Functionality

#### 1. Quiz Management
- Create new quizzes with customizable settings
- Update existing quiz details
- List all quizzes with pagination
  - Support multiple question types:
  - Multiple Choice Questions (MCQ)
- Categorize questions by difficulty level

#### 3. User Management
- User registration and authentication
- Role-based access control (ADMIN, USER)
- User profile management

#### 5. Result Management
- Calculate scores automatically
- Generate detailed result reports
- Store quiz attempts history
- Performance analytics

---

## API Documentation

### Base URL
```
http://localhost:8080/api/v1
```

---

## Installation Guide

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.6+ or Gradle 7+
- MySQL 8.0+
- OpenRouter Account

### Clone the Repository
```bash
git clone https://github.com/Avishka14/quizbot-springboot-server.git
cd quizbot-springboot-server
```

### Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE quizbot_db;

# Create user (optional)
CREATE USER 'quizbot_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON quizbot_db.* TO 'quizbot_user'@'localhost';
FLUSH PRIVILEGES;
```

### Configure Application Properties
Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/quizbot_db
spring.datasource.username=quizbot_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
app.jwt.secret=your-secret-key-here-make-it-long-and-secure
app.jwt.expiration-ms=86400000
(Builit in Method available for JWT Secrete Generation)

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# DeepSeek API Configuration
deepseek.api.key=

# Logging
logging.level.root=INFO
logging.level.com.quizbot=DEBUG
```

---

## Security Implementation

### Authentication Flow
1. User sends credentials to `/auth/login`
2. Server validates credentials
3. If valid, server generates JWT token
4. Client stores token (localStorage/sessionStorage)
5. Client includes token in Authorization header for subsequent requests
6. Server validates token and grants access


## Future Enhancements


2. **Real-Time Quiz Collaboration**
   - Live quiz sessions
   - WebSocket integration
   - Real-time leaderboards

3. **Advanced Analytics**
   - Detailed performance reports
   - Learning curve analysis
   - Topic-wise strength/weakness

4. **Gamification**
   - Badges and achievements
   - Leaderboards
   - Point system

5. **Multi-Language Support**
   - Internationalization (i18n)
   - RTL language support

6. **Mobile App Integration**
   - Dedicated mobile APIs
   - Push notifications

7. **Export/Import Functionality**
   - Export quizzes to PDF
   - Import from CSV/Excel
   - SCORM compliance

---

## Contributing

### How to Contribute
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Logs
Check application logs in:
- Console output during development
- `logs/application.log` in production

---

## License
This project is licensed under the MIT License - see the LICENSE file for details.


---

**Last Updated**: February 2026  
**Version**: 1.0.0
