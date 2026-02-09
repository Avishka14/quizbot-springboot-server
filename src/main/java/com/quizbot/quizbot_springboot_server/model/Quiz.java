package com.quizbot.quizbot_springboot_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String topic;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "JSON")
    private String options;

    private String correctAnswer;
    private String userAnswer;

    private boolean isCorrect;

}
