package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.DescribeDto;
import com.quizbot.quizbot_springboot_server.service.DeepSeekService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/describe")
@RequiredArgsConstructor
public class DescribeController {

    private final DeepSeekService deepSeekService;

    @PostMapping("/getdescribe")
    public ResponseEntity<?> generateDescribe(@RequestBody Map<String , String> request){
        String topic = request.get("topic");
        String userId = request.get("userId");

        try {

          List<DescribeDto> describe = deepSeekService.generateDescribeAndSave(topic , userId);
          return ResponseEntity.ok(describe);

        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal Server Error" +e.getMessage());
        }


    }


}
