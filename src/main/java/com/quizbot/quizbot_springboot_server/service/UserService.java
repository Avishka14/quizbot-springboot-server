package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.LoginRequestDTO;
import com.quizbot.quizbot_springboot_server.dto.UserDTO;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    public User createUser(UserDTO userDTO){
        if(userRepo.existsByEmail(userDTO.getEmail())){
            throw new RuntimeException("Email is Already Used");
        }
        return userRepo.save(modelMapper.map(userDTO , User.class));
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

    public UserResponseDTO logIn(LoginRequestDTO loginRequestDTO){
        Optional<User> optionalUser = userRepo.findByEmail(loginRequestDTO.getEmail());

        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getPassword().equals(loginRequestDTO.getPassword())){
                return modelMapper.map(user, UserResponseDTO.class);
            }
        }
          return null;
    }

}
