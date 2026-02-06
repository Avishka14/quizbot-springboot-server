package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.service.AdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private AdminServices adminServices;

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody User adminUser) {
        try {
            User createdAdmin = adminServices.createAdmin(adminUser);
            createdAdmin.setPassword(null);
            return ResponseEntity.ok(createdAdmin);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
}
