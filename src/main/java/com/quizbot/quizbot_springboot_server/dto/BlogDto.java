package com.quizbot.quizbot_springboot_server.dto;

import com.quizbot.quizbot_springboot_server.model.ApprovalStatus;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDto {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String title;
    private String category;

    private String description;

    private String coverImage;

    private String userid;

    private String userName;

    private LocalDate createdDate;

    private ApprovalStatus approvalStatus;

}
