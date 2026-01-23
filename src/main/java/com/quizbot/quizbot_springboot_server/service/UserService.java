package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.LoginRequestDTO;
import com.quizbot.quizbot_springboot_server.dto.UserDTO;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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


    public User createUser(UserDTO userDTO){
        if(userRepo.existsByEmail(userDTO.getEmail())){
            throw new RuntimeException("Email is Already Used");
        }

        User user = modelMapper.map(userDTO , User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return userRepo.save(user);

    }

    public String generateToken(String email , Long userId) {
        return jwtService.generateToken(email , userId);
    }

    public List<UserDTO> getAllUsers(){
        List<User> userList = userRepo.findAll();
        return modelMapper.map(userList , new TypeToken<List <UserDTO>>() {} .getType());
    }

    public UserDTO getUserById(Long userId){
        Optional<User> userOptional = userRepo.findById(userId);

        if(userOptional.isEmpty()){
            return null;
        }

        User user  = userOptional.get();
        return modelMapper.map(user , UserDTO.class);

    }

    public UserResponseDTO logIn(LoginRequestDTO loginRequestDTO) {
        Optional<User> optionalUser = userRepo.findByEmail(loginRequestDTO.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
                return modelMapper.map(user, UserResponseDTO.class);
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
