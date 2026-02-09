package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.QuizQuestionDTO;
import com.quizbot.quizbot_springboot_server.dto.QuizRequestDTO;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import com.quizbot.quizbot_springboot_server.service.DeepSeekService;
import com.quizbot.quizbot_springboot_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/getquiz")
    public ResponseEntity<?> generatedQuiz(@RequestBody QuizRequestDTO request,
                                           @CookieValue("auth_token") String token) {
         String topic = request.getTopic();
         String difficulty = request.getDifficulty();
         int questionCount = request.getQuestionCount();
        try {

            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            UserResponseDTO user = userService.getUserFromJWTToken(token);

            List<QuizQuestionDTO> quiz = deepSeekService.generateQuizAndSave(topic, difficulty , questionCount,  user.getId());
            return ResponseEntity.ok(quiz);


        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @PostMapping("/submit/{quizId}")
    public ResponseEntity<QuizQuestionDTO> submitAnswer(
            @PathVariable Long quizId,
            @RequestBody Map<String, String> request) {

        String userAnswer = request.get("userAnswer");
        QuizQuestionDTO result = deepSeekService.submitAnswer(quizId, userAnswer);
        return ResponseEntity.ok(result);
    }

}
