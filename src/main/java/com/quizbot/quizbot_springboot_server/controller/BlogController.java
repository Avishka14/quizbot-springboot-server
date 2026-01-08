package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import com.quizbot.quizbot_springboot_server.service.BlogServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/blog")
@CrossOrigin
public class BlogController {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private BlogServices blogServices;

    private final String uploadDir = "uploads";

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @PostMapping("/upload")
    public ResponseEntity<?> hanldeBlogCreate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("content") String content,
            @RequestParam("userId") String userId
            ){

        try {

            BlogDto blogDto = new BlogDto();
            blogDto.setTitle(title);
            blogDto.setCategory(category);
            blogDto.setDescription(content);
            blogDto.setUserid(userId);

            ResponseDTO response = blogServices.createNewBlog(blogDto , file);

            if(response.isStatus()){
                return ResponseEntity.ok(response);
            }else{
                return ResponseEntity.badRequest().body(response);
            }

        }catch (Exception e){
            logger.error("Error while creating blog Error :" , e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file"));
        }


    }


    @GetMapping("/getblog/{userid}")
    public ResponseEntity<List<BlogDto>> getBlogsByUser(@PathVariable String userid) {
        System.out.println("Fetching blogs for userid: " + userid);
        List<BlogDto> blogs;
        try {
            blogs = blogServices.getArticlesByUserId(userid);
            System.out.println("Found " + blogs.size() + " blogs.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(blogs);

    }

    @PostMapping("/updateblog/{id}")
    public ResponseEntity<?> handleBlogUpdate(
            @PathVariable String id,
            @RequestParam(value = "coverImage", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String content,
            @RequestParam("userid") String userId
    ) {
        try {

            BlogDto blogDto = new BlogDto();
            blogDto.setId(id);
            blogDto.setTitle(title);
            blogDto.setCategory(category);
            blogDto.setDescription(content);
            blogDto.setUserid(userId);

            ResponseDTO response = blogServices.updateExistingBlog(blogDto , file);

            if(response.isStatus()){
                return ResponseEntity.ok(response);
            }else{
                return ResponseEntity.badRequest().body(response);
            }

        }catch (Exception e){
            logger.error("Error while updating blog Error :" , e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update file"));
        }

    }

    @GetMapping("/getall")
    public ResponseEntity<List<BlogDto>> getAllBlogs(){
        List<BlogDto> blogs;
        try {

           blogs = blogServices.getAllBlogs();


        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

         return ResponseEntity.ok(blogs);
    }

}
