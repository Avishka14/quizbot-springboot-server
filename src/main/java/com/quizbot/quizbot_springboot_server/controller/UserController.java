package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.LoginRequestDTO;
import com.quizbot.quizbot_springboot_server.dto.UserDTO;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.dto.UserStatsDTO;
import com.quizbot.quizbot_springboot_server.model.Quiz;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import com.quizbot.quizbot_springboot_server.service.CookieService;
import com.quizbot.quizbot_springboot_server.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private JWTService jwtService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/createuser")
    public ResponseEntity<?> createUser(
            @Valid @RequestBody User user,
            HttpServletResponse response
    ) {
        try {

            User createdUser = userService.createUser(user);
            String token = userService.generateToken(createdUser.getEmail() , createdUser.getId() , user );

            String authCookie = cookieService.buildAuthCookie(token);
            response.addHeader("Set-Cookie" , authCookie);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("user", createdUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);

        } catch (Exception e) {
            logger.error("Exception in User Controller Exception: " + e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> logIn(
            @Valid @RequestBody LoginRequestDTO loginRequestDTO,
            HttpServletResponse response
    ) {
        User userResponse = userService.logIn(loginRequestDTO);

        if (userResponse != null) {

            String token = userService.generateToken(userResponse.getEmail() , userResponse.getId() , userResponse);

            String authCookie = cookieService.buildAuthCookie(token);
            response.addHeader("Set-Cookie" , authCookie);

            return ResponseEntity.ok(userResponse);

        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @GetMapping("/getbytoken")
    public ResponseEntity<?> getUserbyJWTToken( @CookieValue("auth_token") String token){

        try{

            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            UserResponseDTO user = userService.getUserFromJWTToken(token);
            return ResponseEntity.ok(user);

        }catch (Exception e){
            logger.error("Exception in User Controller Exception: " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }
    }


    @GetMapping("/getquestions")
    public ResponseEntity<?> getPreviousQuestions(@CookieValue("auth_token") String token){

        try{

            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            UserResponseDTO user = userService.getUserFromJWTToken(token);
            List<Quiz> quizez = userService.getUserPreviousQuestions(user.getEmail());
            return ResponseEntity.ok(quizez);

        }catch (Exception e){
            logger.error("Exception in User Controller Exception: " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }

    }

    @GetMapping("/getstats")
    public ResponseEntity<?> getUserStats(@CookieValue("auth_token") String token){

        try{
            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }
            UserResponseDTO user = userService.getUserFromJWTToken(token);

            UserStatsDTO stats = userService.getUserStats(user.getEmail());
            return ResponseEntity.ok(stats);

        }catch (Exception e){
            logger.error("Exception in User Controller Exception: " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred");
        }

    }




}
