package com.quizbot.quizbot_springboot_server.repository;

import com.quizbot.quizbot_springboot_server.model.Role;
import com.quizbot.quizbot_springboot_server.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository <Role , Long> {
    Optional<Role> findByName(RoleType name);
}
