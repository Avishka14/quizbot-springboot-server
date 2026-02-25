# QuizBot (Beackemd) - Project Documentation


---

## Project Overview

### Description
The purpose of the QuizBot project is to develop an interactive learning platform that enhances students’ knowledge through structured quizzes and concept-based learning. The system enables users to assess their understanding across various subjects by participating in quizzes designed to reinforce learning outcomes.

Additionally, QuizBot provides clear and concise explanations of both technical and non-technical keywords to support conceptual clarity and deeper subject comprehension. The platform also includes a blogging feature that allows students to create, publish, and share articles within the QuizBot community, promoting collaborative learning and knowledge exchange.


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

<img width="1328" height="805" alt="Image" src="https://github.com/user-attachments/assets/0be7a080-469d-4c54-9a50-1036eeb744d6" />

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
- **Mockito** : Unit Testing

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
- Generate quizzes based on selected topics
- Categorize quizzes by difficulty levels:
  - Beginner
  - Intermediate
  - Expert
- Allow users to select the number of questions per quiz:
  - 5 questions
  - 10 questions
  - 15 questions
- Support Multiple Choice Questions (MCQ)
- Provide real-time answer evaluation
- Display instant quiz results after submission

#### 2. Definition Management
- Generate definitions for technical keywords
- Generate definitions for non-technical keywords
- Provide clear and concise explanations
- Enable keyword search functionality

#### 3. User Management
- User registration and authentication
- Role-based access control (ADMIN, USER)
- User profile management

#### 4. Blog Management
- Create and publish blog posts
- Edit and manage personal blogs
- Share blogs with the QuizBot community
- Browse and read community blogs

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

### End Points 

- Quiz Controller -> DeepSeek Service

| Endpooint | Method | Description | Request | Response
|----------|----------|----------|----------|----------|
| ``` /quiz/getquiz ``` | POST | Generate a new quiz based on topic, difficulty, and question count |JSON  + auth_token (Cookie) |List of QuizQuestionDTO |
| ``` quiz/submit/{quizId} ``` | POST |Submit an answer for a quiz question |Path Variable: quizId + JSON  | QuizQuestionDTO with updated answer 

- Describe Controller -> DeepSeek Service

| Endpooint | Method | Description | Request | Response
|----------|----------|----------|----------|----------|
| ```/describe/getdescribe ``` | POST | Generate a short description for a given topic and save it for the user |JSON + auth_token (Cookie) |List of DescribeDto |


- Blog Controller -> Blog Service

| Endpooint | Method | Description | Request | Response
|----------|----------|----------|----------|----------|
| ``` /blog/upload ``` | POST | Create a New Blog | multipart/form-data → file, title, category, content + auth_token (Cookie) | Success/Failure message (JSON) |
| ``` /blog/getuserblogs ``` | GET | Get blogs created by authenticated user |auth_token (Cookie) | List of BlogDto (JSON)
| ``` /blog/updateblog/{id} ``` | PUT | Update existing blog |multipart/form-data → title, category, description, optional coverImage + auth_token (Cookie) |Success/Failure message (JSON) |
| ``` /blog/blog/getall ```  | GET | Get all approved blogs | Non | List of approved blogs (JSON) |
| ``` /blog/getnotapproved ``` | GET | Get all pending blogs (Admin) | None | List of pending blogs (JSON) |
| ``` /blog/approve/{id} ```  | PUT | Approve a blog (Admin) |Path Variable: blog ID| Success message (JSON) |
| ``` /blog/decline/{id} ``` | PUT 1 C2 | Decline a blog (Admin) | Path Variable: blog ID | Success message (JSON) |

- User Controller -> User Service

| Endpooint | Method | Description | Request | Response
|----------|----------|----------|----------|----------|
| ``` /users/createuser ``` | POST | Create a new user and return JWT in cookie | JSON  | JSON → Cookie |
| ``` /users/getbytoken ``` | GET | Get user info from JWT token | Cookie → auth_token |JSON → UserResponseDTO |
| ```/users/getquestions``` | GET | Get all previous quizzes taken by the authenticated user | Cookie → auth_token |List of Quiz |
| ```/users/getstats``` | GET |Get user statistics: days logged in, questions covered, topics covered | Cookie → auth_token |UserStatsDTO |


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
