package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.DescribeDto;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import com.quizbot.quizbot_springboot_server.service.CookieService;
import com.quizbot.quizbot_springboot_server.service.DeepSeekService;
import com.quizbot.quizbot_springboot_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/describe")
@RequiredArgsConstructor
public class DescribeController {

    private final DeepSeekService deepSeekService;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/getdescribe")
    public ResponseEntity<?> generateDescribe(@RequestBody Map<String , String> request ,
            @CookieValue("auth_token") String token ){
        String topic = request.get("topic");
        try {

            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            UserResponseDTO user = userService.getUserFromJWTToken(token);

          List<DescribeDto> describe = deepSeekService.generateDescribeAndSave(topic , user.getId());
          return ResponseEntity.ok(describe);

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error" +e.getMessage());
        }


    }


}
