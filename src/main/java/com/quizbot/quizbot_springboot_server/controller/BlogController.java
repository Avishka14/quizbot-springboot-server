package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import com.quizbot.quizbot_springboot_server.dto.UserResponseDTO;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import com.quizbot.quizbot_springboot_server.service.BlogServices;
import com.quizbot.quizbot_springboot_server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("api/v1/blog")
@CrossOrigin
public class BlogController {

    @Autowired
    private BlogServices blogServices;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @PostMapping("/upload")
    public ResponseEntity<?> handleBlogCreate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("content") String content,
            @CookieValue("auth_token") String token
    ) {
        try {

            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            UserResponseDTO user = userService.getUserFromJWTToken(token);

            BlogDto blogDto = new BlogDto();
            blogDto.setTitle(title);
            blogDto.setCategory(category);
            blogDto.setDescription(content);
            blogDto.setUserid(user.getId().toString());

            ResponseDTO response = blogServices.createNewBlog(blogDto, file );

            if (response.isStatus()) {
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Failed to create blog for user: {}", user.getId());
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            logger.error("Error while creating blog for user:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file"));
        }
    }

    @GetMapping("/getuserblogs")
    public ResponseEntity<?> getBlogsByUserToken(
            @CookieValue("auth_token") String token
    ){

        try{

            if(!jwtService.validateToken(token)){
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or Expired token");
            }

            List<BlogDto> blogs = blogServices.getBlogsbyToken(token);
            return ResponseEntity.ok(blogs);

        } catch (Exception e) {
            logger.error("Error fetching blogs for authHeader: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch blogs"));
        }

    }

    @PutMapping("/updateblog/{id}")
    public ResponseEntity<?> handleBlogUpdate(
            @PathVariable String id,
            @RequestParam(value = "coverImage", required = false) MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String content,
            @CookieValue("auth_token") String token
    ) {
        try {

            if (!jwtService.validateToken(token)) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid or expired token");
            }

            UserResponseDTO user = userService.getUserFromJWTToken(token);

            BlogDto blogDto = new BlogDto();
            blogDto.setId(id);
            blogDto.setTitle(title);
            blogDto.setCategory(category);
            blogDto.setDescription(content);
            blogDto.setUserid(user.getId().toString());

            ResponseDTO response = blogServices.updateExistingBlog(blogDto, file);

            if (response.isStatus()) {
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Failed to update blog with id: {}", id);
                return ResponseEntity.badRequest().body(response);
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Blog not found"));

        } catch (Exception e) {
            logger.error("Error while updating blog with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update blog"));
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<?> getAllBlogs() {
        try {

            List<BlogDto> blogs = blogServices.getAllBlogs();
            return ResponseEntity.ok(blogs);

        } catch (Exception e) {
            logger.error("Error fetching all blogs", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch blogs"));
        }
    }

    @GetMapping("/getnotapproved")
    public ResponseEntity<?> getNotApprovedBlogs(){

        try{
            List<BlogDto> blogs = blogServices.getNotApprovedBlogs();
            return ResponseEntity.ok(blogs);
        }catch (RuntimeException e){
            logger.error("Error fetching not approved blogs :", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred"));
        }

    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveBlog(@PathVariable String id) {
        try {
            ResponseDTO responseDTO = blogServices.approveBlog(id);
            return ResponseEntity.ok(responseDTO);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Blog not found"));

        } catch (Exception e) {
            logger.error("Error approving blog with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PutMapping("/decline/{id}")
    public ResponseEntity<?> delcineBlog(@PathVariable String id) {
        try {
            ResponseDTO responseDTO = blogServices.declineBlog(id);
            return ResponseEntity.ok(responseDTO);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Blog not found"));

        } catch (Exception e) {
            logger.error("Error decline blog with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }






}