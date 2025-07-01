package com.quizbot.quizbot_springboot_server.controller;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import com.quizbot.quizbot_springboot_server.service.BlogServices;
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

    @PostMapping("/upload")
    public ResponseEntity<?> hanldeBlogUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("content") String content,
            @RequestParam("userId") String userId
            ){

        try {
            // Save file to folder
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/files/" + fileName;

            // Save blog info to DB
            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setCategory(category);
            blog.setDescription(content);
            blog.setCoverImage(fileUrl);
            blog.setUserid(userId);
            blogRepo.save(blog);

            return ResponseEntity.ok().body(Map.of("message", "Article uploaded successfully!"));

        } catch (IOException e) {
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
            // Find existing blog by id (handle if not found)
            Optional<Blog> optionalBlog = blogRepo.findById(id);
            if (optionalBlog.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Blog not found"));
            }
            Blog blog = optionalBlog.get();

            // If new file uploaded, save it and update coverImage path
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path path = Paths.get(uploadDir, fileName);
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = "/files/" + fileName;
                blog.setCoverImage(fileUrl);
            }

            // Update other fields
            blog.setTitle(title);
            blog.setCategory(category);
            blog.setDescription(content);
            blog.setUserid(userId);

            // Save updated blog
            blogRepo.save(blog);

            return ResponseEntity.ok(Map.of("message", "Article updated successfully!"));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload file"));
        }
    }

}
