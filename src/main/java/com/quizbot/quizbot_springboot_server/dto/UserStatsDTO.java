package com.quizbot.quizbot_springboot_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsDTO {
    private long userId;
    private long daysLogged;
    private long questionsCovered;
    private long topicsCovered;
    private String name;

}
