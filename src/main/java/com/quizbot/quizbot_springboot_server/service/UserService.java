package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.LoginRequestDTO;
import com.quizbot.quizbot_springboot_server.dto.UserDTO;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.model.Role;
import com.quizbot.quizbot_springboot_server.model.RoleType;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.RoleRepo;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private RoleRepo roleRepo;


    public User createUser(User user){
        if(userRepo.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email is Already Used");
        }

        user.setJoined_date(LocalDate.now());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepo.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        user.setRoles(Set.of(userRole));

        return userRepo.save(user);

    }

    public String generateToken(String email , Long userId , User user ) {
        return jwtService.generateToken(email , userId , user);
    }


    public User logIn(LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepo.findByEmail(loginRequestDTO.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
                return user;
            }
        }

        return null;
    }

    public UserResponseDTO getUserFromJWTToken(String token) {

            String email = jwtService.extractEmail(token);

            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Token does not contain valid email");
            }
            Optional<User> user = userRepo.findByEmail(email);

            if (user.isPresent()) {
                return modelMapper.map(user.get(), UserResponseDTO.class);
            } else {
                throw new NoSuchElementException("User not found with email: " + email);
            }

    }



}
