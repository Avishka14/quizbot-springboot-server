# QuizBot Spring Boot 

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

- **Spring Data JPA**

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

 <img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/d8fd7826-50ea-4d05-9c4a-98efa1975248" />
 <img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/8ca5f5e5-f3b2-430a-824b-8eb0df95bfe8" />
 <img width="1919" height="1079" alt="Image" src="https://github.com/user-attachments/assets/442c4601-a817-47dd-9823-87a1b36df777" />
 <img width="1887" height="959" alt="Image" src="https://github.com/user-attachments/assets/c343db50-ef13-49e4-a88e-3f399894d961" />
 <img width="1887" height="959" alt="Image" src="https://github.com/user-attachments/assets/9c9d11dd-5647-45d2-bcdd-4a13e0fa342e" />
 <img width="1908" height="924" alt="Image" src="https://github.com/user-attachments/assets/70c0db0e-8e78-4bef-b58a-b19948980d51" />
 <img width="1906" height="975" alt="Image" src="https://github.com/user-attachments/assets/9641e846-b663-4561-a14f-89700ae94fdc" />
 <img width="1900" height="907" alt="Image" src="https://github.com/user-attachments/assets/436b2aa7-43d7-427e-812d-42988c158c07" />
 <img width="1903" height="934" alt="Image" src="https://github.com/user-attachments/assets/b1d45e85-e4d5-4e62-8f29-55d2d826e05d" />
 <img width="1902" height="925" alt="Image" src="https://github.com/user-attachments/assets/caba0ca2-44d7-4578-b812-e5463ad7f75b" />
 <img width="1899" height="938" alt="Image" src="https://github.com/user-attachments/assets/66202148-edf8-45df-a14d-ba9910c74a05" />
 <img width="1884" height="926" alt="Image" src="https://github.com/user-attachments/assets/5ca18857-08c7-4b20-939e-524fa9474449" />
 <img width="1903" height="935" alt="Image" src="https://github.com/user-attachments/assets/c99db151-9a4b-438c-9400-cc96cc08e36f" />
 <img width="1900" height="935" alt="Image" src="https://github.com/user-attachments/assets/a68e7d07-cc7e-4a39-8399-ed3207e94649" />
 
