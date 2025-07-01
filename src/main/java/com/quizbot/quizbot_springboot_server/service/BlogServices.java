package com.quizbot.quizbot_springboot_server.service;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServices {

    @Autowired
    private BlogRepo blogRepo;

    @Autowired
    private ModelMapper modelMapper;

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


}
