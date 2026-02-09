package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.controller.UserController;
import com.quizbot.quizbot_springboot_server.model.Role;
import com.quizbot.quizbot_springboot_server.model.RoleType;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.RoleRepo;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class AdminServices {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleRepo roleRepo;

    private static final Logger logger = LoggerFactory.getLogger(AdminServices.class);

    public User createAdmin(User user){
        if(userRepo.existsByEmail(user.getEmail())){
            logger.warn("Admin creation failed - email already exists: {}", user.getEmail());
            throw new RuntimeException("Email is Already Used");
        }

        user.setJoined_date(LocalDate.now());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepo.findByName(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> {
                    logger.error("ROLE_ADMIN not found in database");
                    return new RuntimeException("ROLE_ADMIN not found");
                });

        user.setRoles(Set.of(userRole));
        return userRepo.save(user);

    }
}
