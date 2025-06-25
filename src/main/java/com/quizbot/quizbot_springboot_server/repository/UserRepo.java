package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User , Long> {
    boolean existsByEmail(String email);
}
