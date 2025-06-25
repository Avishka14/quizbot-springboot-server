package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepo extends JpaRepository <Blog, Long> {

}
