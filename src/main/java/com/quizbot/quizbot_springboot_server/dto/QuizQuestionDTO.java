package com.quizbot.quizbot_springboot_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizQuestionDTO {
    private String question;
    private List<String> options;
    private String answer;
}
