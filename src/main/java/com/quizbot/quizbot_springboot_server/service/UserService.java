package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.UserDTO;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;

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

}
