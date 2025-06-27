package com.quizbot.quizbot_springboot_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")

public class User {

    @Id
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    private String joined_date;

    @PrePersist
    protected void onCreate(){
        this.joined_date = String.valueOf(LocalDate.now());
    }

}
