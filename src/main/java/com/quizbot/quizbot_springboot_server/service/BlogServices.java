package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import org.modelmapper.ModelMapper;
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

    public ResponseDTO createNewBlog(BlogDto blogDto , MultipartFile file ){

        try{
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/files/" + fileName;

            // Save blog info to DB
            Blog blog = new Blog();
            blog.setTitle(blogDto.getTitle());
            blog.setCategory(blogDto.getCategory());
            blog.setDescription(blogDto.getDescription());
            blog.setCoverImage(fileUrl);
            blog.setUserid(blogDto.getUserid());
            blog.setApproval(false);
            blogRepo.save(blog);

            return new ResponseDTO(true);


        }catch (IOException ioException){
          return  new ResponseDTO(false);

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
