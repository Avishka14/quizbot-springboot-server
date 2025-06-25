package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.UserDTO;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/createuser")
    public User createUser(@RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
    }

    @GetMapping("/getusers")
    public List<UserDTO> getUser(){
        return userService.getAllUsers();
    }

}
