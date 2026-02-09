package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.Describe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DescribeRepo extends JpaRepository<Describe , Long> {
  long countByUserId(Long userId);
}
