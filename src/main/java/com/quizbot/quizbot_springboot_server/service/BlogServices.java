package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import com.quizbot.quizbot_springboot_server.utility.HelperMethods;
import lombok.experimental.Helper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;



@Service
public class BlogServices {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HelperMethods helperMethods;

    private final String uploadDir = "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Logger logger = LoggerFactory.getLogger(BlogServices.class);

    public ResponseDTO createNewBlog(BlogDto blogDto, MultipartFile file) {

        ResponseDTO validationError = helperMethods.validateBlogInput(blogDto, file, true);
        if (validationError != null) {
            return validationError;
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);
        String fileUrl = "/files/" + fileName;

        try {
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            Blog blog = new Blog();
            blog.setTitle(blogDto.getTitle());
            blog.setCategory(blogDto.getCategory());
            blog.setDescription(blogDto.getDescription());
            blog.setCoverImage(fileUrl);
            blog.setUserid(blogDto.getUserid());
            blog.setApproval(false);

            blogRepo.save(blog);

            logger.info("Blog created successfully for user: {}", blogDto.getUserid());
            return new ResponseDTO(true, "Blog Created Success");

        } catch (Exception exception) {
            logger.error("Error while creating the blog", exception);
            helperMethods.deleteFileIfExists(path);
            return new ResponseDTO(false, "Error while creating the blog");
        }
    }

    public List<BlogDto> getArticlesByUserId(String userId) {
        logger.info("Fetching articles for user: {}", userId);
        List<Blog> blogs = blogRepo.findByUserid(userId);

        return blogs.stream()
                .map(blog -> modelMapper.map(blog, BlogDto.class))
                .collect(Collectors.toList());
    }

    public List<BlogDto> getAllBlogs() {
        logger.info("Fetching all blogs");
        List<Blog> blogs = blogRepo.findAll();

        return blogs.stream()
                .map(blog -> modelMapper.map(blog, BlogDto.class))
                .collect(Collectors.toList());
    }

    public ResponseDTO updateExistingBlog(BlogDto blogDto, MultipartFile file) {

        if (blogDto.getId() == null || blogDto.getId().isBlank()) {
            logger.error("Blog id is missing from api call");
            return new ResponseDTO(false, "Blog Id is Missing");
        }

        Optional<Blog> optionalBlog = blogRepo.findById(blogDto.getId());
        if (optionalBlog.isEmpty()) {
            logger.warn("Blog not found with ID: {}", blogDto.getId());
            return new ResponseDTO(false, "Blog cannot be found");
        }

        Blog blog = optionalBlog.get();

        ResponseDTO validationError = helperMethods.validateBlogInput(blogDto, file, false);
        if (validationError != null) {
            return validationError;
        }

        Path path = null;
        String oldImageUrl = blog.getCoverImage();

        try {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                path = Paths.get(uploadDir, fileName);
                String fileUrl = "/files/" + fileName;

                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                blog.setCoverImage(fileUrl);

                helperMethods.deleteOldImageFile(oldImageUrl);
            }

            blog.setTitle(blogDto.getTitle());
            blog.setDescription(blogDto.getDescription());
            blog.setCategory(blogDto.getCategory());
            blog.setApproval(false);
            blog.setUserid(blogDto.getUserid());

            blogRepo.save(blog);

            logger.info("Blog updated successfully with ID: {}", blogDto.getId());
            return new ResponseDTO(true, "Update Success");

        } catch (Exception e) {
            logger.error("Error while updating blog with ID: {}", blogDto.getId(), e);

            if (path != null) {
                helperMethods.deleteFileIfExists(path);
            }

            return new ResponseDTO(false, "Internal server error occurred");
        }
    }

    public ResponseDTO approveBlog(String blogId) {
        logger.info("Approving blog with ID: {}", blogId);

        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new NoSuchElementException("Blog not found with id: " + blogId));

        blog.setApproval(true);
        blogRepo.save(blog);

        logger.info("Blog approved successfully with ID: {}", blogId);
        return new ResponseDTO(true, "Approval Success");
    }

}