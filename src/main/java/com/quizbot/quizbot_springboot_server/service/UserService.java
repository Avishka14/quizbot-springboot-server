package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.*;
import com.quizbot.quizbot_springboot_server.model.Quiz;
import com.quizbot.quizbot_springboot_server.model.Role;
import com.quizbot.quizbot_springboot_server.model.RoleType;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.DescribeRepo;
import com.quizbot.quizbot_springboot_server.repository.QuizRepo;
import com.quizbot.quizbot_springboot_server.repository.RoleRepo;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private QuizRepo quizRepo;

    @Autowired
    private DescribeRepo describeRepo;


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

    public List<Quiz> getUserPreviousQuestions(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found for :" +email));

        List<Quiz> quizzes = quizRepo.findByUserId(user.getId());

        return quizzes != null ? quizzes : Collections.emptyList();

    }

    public UserStatsDTO getUserStats(String email){

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found for :" +email));

        LocalDate joinedDate = user.getJoined_date();
        LocalDate today = LocalDate.now();

        long daysSinceLogged = ChronoUnit.DAYS.between(joinedDate , today);
        long totalQuizzes = quizRepo.countByUserId(user.getId());
        long totalDescribes = describeRepo.countByUserId(user.getId());

        UserStatsDTO userStatsDTO = new UserStatsDTO();
        userStatsDTO.setUserId(user.getId());
        userStatsDTO.setDaysLogged(daysSinceLogged);
        userStatsDTO.setQuestionsCovered(totalQuizzes);
        userStatsDTO.setTopicsCovered(totalDescribes);
        userStatsDTO.setName(user.getName());
        return userStatsDTO;

    }



}
