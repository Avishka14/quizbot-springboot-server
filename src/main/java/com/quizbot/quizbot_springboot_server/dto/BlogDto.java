package com.quizbot.quizbot_springboot_server.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {
    private Long id;

    private String title;
    private String category;

    private String description;

    private String coverImage;

    private String userid;
}
