package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepo extends JpaRepository <Blog, String> {
     List<Blog> findByUserid (String userid);
}
