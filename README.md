# QuizBot Spring Boot Server

A **Spring Boot backend** for **QuizBot**, providing REST APIs for user management, AI-powered quiz generation, blog articles, and topic descriptions.  
It uses **MySQL** for persistence and integrates with **DeepSeek / OpenRouter** for AI-generated quizzes and descriptions.

---

## 🚀 Features

- **User Management**  
  Register, login, and retrieve users.

- **Quiz Generation**  
  Generate and store multiple-choice quizzes on any topic using **DeepSeek AI**.

- **Blog Articles**  
  Upload, update, and fetch blog articles with **image support**.

- **Topic Descriptions**  
  Generate and store topic descriptions using **DeepSeek AI**.

- **File Uploads**  
  Handles image uploads for blogs, serving them from the `/files/` endpoint.


---

## 📂 Key Classes

- `QuizbotSpringbootServerApplication` – Main entry point  
- `UserController` – User APIs  
- `QuizController` – Quiz APIs  
- `BlogController` – Blog APIs  
- `DescribeController` – Description APIs  
- `DeepSeekService` – Integration with DeepSeek/OpenRouter  

---

## 🔑 API Endpoints

### User
- **POST** `/api/v1/users/createuser` → Register a new user  
- **POST** `/api/v1/users/login` → Login with email and password  
- **GET** `/api/v1/users/getusers/{id}` → Get user by ID  

### Quiz
- **POST** `/api/v1/quiz/getquiz` → Generate and save quiz questions for a topic and user
- 
### Blog
- **POST** `/api/v1/blog/upload` → Upload a new blog article with an image  
- **POST** `/api/v1/blog/updateblog/{id}` → Update an existing blog article  
- **GET** `/api/v1/blog/getblog/{userid}` → Get all blogs for a user  
- **GET** `/api/v1/blog/getall` → Get all blogs  

### Topic Description
- **POST** `/api/v1/describe/getdescribe` → Generate and save a topic description for a user  

### File Access
- **GET** `/files/{filename}` → Access uploaded images  

---

##  Configuration

Edit `application.properties` with your settings:

```properties
# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/quizbot
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# DeepSeek API key
deepseek.api.key=your_api_key

# File upload settings
file.upload-dir=files

```

## Tech Stack

- **Java 17**

- **Spring Boot**

- **Spring Data JPA (Hibernate)**

- **MySQL**

- **DeepSeek / OpenRouter API**

- **Maven**

## Project Structure  

```
quizbot-springboot-server/
│
├── src/
│   ├── main/
│   │   ├── java/com/quizbot/quizbot_springboot_server/
│   │   │   ├── controller/       >> REST controllers
│   │   │   ├── dto/              >> Data Transfer Objects
│   │   │   ├── model/            >> JPA entities
│   │   │   ├── repository/       >> Spring Data JPA repositories
│   │   │   ├── service/          >> Business logic and AI integration
│   │   │   └── config/           >> Web and CORS configuration
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/quizbot/quizbot_springboot_server/
│           └── QuizbotSpringbootServerApplicationTests.java
├── uploads/                     >> Uploaded images
├── pom.xml                      >> Maven build file
├── mvnw, mvnw.cmd               >> Maven wrapper scripts
└── .gitignore, .gitattributes
```
## Run this Project

Back end Setup

1.Clone the Repository and Open it on InteliJ or any IDE.

2.Configure Database & API Keys - Edit application.properties


```bash
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
deepseek.api.key=YOUR_DEEPSEEK_API_KEY
```
Make sure your MySQL server is running and the quizbotdb database exists. (SQL file included in the Db Folder)

3.Build the project
In the terminal, run:

```bash
./mvnw clean install
```

3.Start back end Server


```bash
./mvnw spring-boot:run
```

```The API will be available at http://localhost:8080.```

3.Clone the Front end Repository https://github.com/Avishka14/quizbot-reactjs-client

4. Accessing the Application
Frontend: Open your browser to http://localhost:5173 (or the port your React app uses).
Backend API: Available at http://localhost:8080.

Note:
CORS is configured to allow requests from http://localhost:5173. If your frontend runs on a different port, update the CORS settings in the backend configuration.

---

## 🚧 Project Status

This project is **currently under development**.  
In the future, the goal is to:
- Introduce a **pricing system**  
- Release as a **SaaS platform**  
- Provide **premium options** that enhance quiz generation and study assistance  

Stay tuned for upcoming releases!

---

## 🤝 Contributions

Contributions are welcome!  
If you’d like to improve this project, feel free to fork the repository and submit a pull request.  

---

<p align="center">
  <sub> &copy; <b>Avishka14 | 2025</b></sub>
</p>
