package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BlogServices {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private ModelMapper modelMapper;

    private final String uploadDir = "uploads";
    private static final long maxFileSize = 5 * 1024 * 1024;
    private static final Logger logger = LoggerFactory.getLogger(BlogServices.class);


    public ResponseDTO createNewBlog(BlogDto blogDto , MultipartFile file ){

        if (blogDto.getTitle() == null || blogDto.getTitle().isBlank()) {
            return new ResponseDTO(false, "Title cannot be empty");
        }

        if (blogDto.getTitle().length() > 100) {
            return new ResponseDTO(false, "Title must be 100 characters or less");
        }

        if (file == null || file.isEmpty()) {
            return new ResponseDTO(false, "Cover image is required");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return new ResponseDTO(false, "Only image files are allowed");
        }

        if (file.getSize() > maxFileSize) {
            return new ResponseDTO(false, "Image size must be less than 5MB");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);
        String fileUrl = "/files/" + fileName;


        try{

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

            return new ResponseDTO(true , "Blog Created Success");


        }catch (Exception exception){

            logger.error("Error while creating the blog Exception: ", exception);

            try{
             Files.deleteIfExists(path);
            }catch (IOException e) {
                logger.error("Failed to delete temporary file: {}", path, e);
            }
            logger.error("Error while creating the blog", exception);
            return new ResponseDTO(false, "Error while creating the blog");

        }

    }

    public List<BlogDto> getArticlesByUserId(String userId){
        List<Blog> blogs = blogRepo.findByUserid(userId);


        return blogs.stream()
                .map(blog -> modelMapper.map(blog, BlogDto.class))
                .collect(Collectors.toList());

    }

    public BlogDto updateBlog(String id, BlogDto blogDTO) {
        Blog existingBlog = blogRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        modelMapper.map(blogDTO, existingBlog);

        Blog updatedBlog = blogRepo.save(existingBlog);

        return modelMapper.map(updatedBlog, BlogDto.class);
    }

    public List<BlogDto> getAllBlogs(){
        List<Blog> blogs = blogRepo.findAll();

        return blogs.stream()
                .map(blog -> modelMapper.map(blog, BlogDto.class))
                .collect(Collectors.toList());

    }


}
