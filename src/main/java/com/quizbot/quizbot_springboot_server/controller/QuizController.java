package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.QuizQuestionDTO;
import com.quizbot.quizbot_springboot_server.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final DeepSeekService deepSeekService;

    @PostMapping("/getquiz")
    public ResponseEntity<?> generatedQuiz(
            @RequestParam String topic,
            @RequestParam String userId
    ) {
        try {
            List<QuizQuestionDTO> quiz = deepSeekService.generateQuizAndSave(topic, userId);
            return ResponseEntity.ok(quiz);
        } catch (Exception e) {
            // log the exception (use a logger in real projects)
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

}
