package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.ApprovalStatus;
import com.quizbot.quizbot_springboot_server.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepo extends JpaRepository <Blog, String> {
     List<Blog> findByUserid (String userid);

     List<Blog> findByApprovalStatus(ApprovalStatus status);

     // Find pending blogs
     default List<Blog> findPendingBlogs() {
          return findByApprovalStatus(ApprovalStatus.PENDING);
     }

     // Find approved blogs
     default List<Blog> findApprovedBlogs() {
          return findByApprovalStatus(ApprovalStatus.APPROVED);
     }
}
