package com.quizbot.quizbot_springboot_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")

public class User {

    @Id
    private Long id;

    @Column (unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

}
