package com.quizbot.quizbot_springboot_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizRequestDTO {
    private String topic;
    private String difficulty;
    private int questionCount;
}
