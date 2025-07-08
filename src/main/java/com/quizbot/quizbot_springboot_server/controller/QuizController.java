package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.QuizQuestionDTO;
import com.quizbot.quizbot_springboot_server.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final DeepSeekService deepSeekService;

    @PostMapping("/getquiz")
    public ResponseEntity<?> generatedQuiz(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String userId = request.get("userId");

        try {
            List<QuizQuestionDTO> quiz = deepSeekService.generateQuizAndSave(topic, userId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

}
