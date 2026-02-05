package com.quizbot.quizbot_springboot_server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionDTO {
    private long id;
    private String question;
    private List<String> options;
    private String answer;
    private String userAnswer;
    private boolean isCorrect;
}
