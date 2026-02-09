package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepo extends JpaRepository<Quiz, Long> {
 List<Quiz> findByUserId(Long userId);
 long countByUserId(Long userId);
}

