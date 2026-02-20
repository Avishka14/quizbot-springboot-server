package com.quizbot.quizbot_springboot_server.services;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import com.quizbot.quizbot_springboot_server.model.ApprovalStatus;
import com.quizbot.quizbot_springboot_server.model.Blog;
import com.quizbot.quizbot_springboot_server.model.User;
import com.quizbot.quizbot_springboot_server.repository.BlogRepo;
import com.quizbot.quizbot_springboot_server.repository.UserRepo;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import com.quizbot.quizbot_springboot_server.service.BlogServices;
import com.quizbot.quizbot_springboot_server.utility.HelperMethods;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServicesTest {

    @InjectMocks
    private BlogServices blogServices;

    @Mock
    private BlogRepo blogRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private HelperMethods helperMethods;

    @Mock
    private JWTService jwtService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private MultipartFile multipartFile;

    private BlogDto blogDto;
    private Blog blog;

    @BeforeEach
    void setup() {
        blogDto = new BlogDto();
        blogDto.setId("1");
        blogDto.setTitle("Test Blog");
        blogDto.setCategory("Tech");
        blogDto.setDescription("Description");
        blogDto.setUserid("1");
        blogDto.setUserName("John");

        blog = new Blog();
        blog.setId(123456789L);
        blog.setTitle("Test Blog");
    }


    @Test
    void createNewBlog_success() throws Exception {

        when(helperMethods.validateBlogInput(blogDto, multipartFile, true))
                .thenReturn(null);

        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream("test".getBytes()));

        ResponseDTO response = blogServices.createNewBlog(blogDto, multipartFile);

        assertTrue(response.isStatus());
        verify(blogRepo, times(1)).save(any(Blog.class));
    }

    @Test
    void createNewBlog_validationFails() {

        when(helperMethods.validateBlogInput(blogDto, multipartFile, true))
                .thenReturn(new ResponseDTO(false, "Validation Error"));

        ResponseDTO response = blogServices.createNewBlog(blogDto, multipartFile);

        assertFalse(response.isStatus());
        verify(blogRepo, never()).save(any());
    }


    @Test
    void getBlogsbyToken_success() {

        String token = "validToken";
        String email = "test@mail.com";

        User user = new User();
        user.setId(3333333333333333333L);
        user.setEmail(email);

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        when(blogRepo.findByUserid(user.getId().toString()))
                .thenReturn(List.of(blog));
        when(modelMapper.map(any(Blog.class), eq(BlogDto.class)))
                .thenReturn(blogDto);

        List<BlogDto> result = blogServices.getBlogsbyToken(token);

        assertEquals(1, result.size());
    }

    @Test
    void getBlogsbyToken_userNotFound() {

        when(jwtService.extractEmail("token")).thenReturn("mail@test.com");
        when(userRepo.findByEmail("mail@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> blogServices.getBlogsbyToken("token"));
    }

    @Test
    void getAllBlogs_success() {

        when(blogRepo.findApprovedBlogs()).thenReturn(List.of(blog));
        when(modelMapper.map(any(Blog.class), eq(BlogDto.class)))
                .thenReturn(blogDto);

        List<BlogDto> result = blogServices.getAllBlogs();

        assertEquals(1, result.size());
    }


    @Test
    void updateExistingBlog_success() throws Exception {

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));
        when(helperMethods.validateBlogInput(blogDto, multipartFile, false))
                .thenReturn(null);

        when(multipartFile.getOriginalFilename()).thenReturn("image.png");
        when(multipartFile.getInputStream())
                .thenReturn(new ByteArrayInputStream("test".getBytes()));

        ResponseDTO response = blogServices.updateExistingBlog(blogDto, multipartFile);

        assertTrue(response.isStatus());
        verify(blogRepo).save(blog);
    }

    @Test
    void updateExistingBlog_blogNotFound() {

        when(blogRepo.findById("1")).thenReturn(Optional.empty());

        ResponseDTO response = blogServices.updateExistingBlog(blogDto, multipartFile);

        assertFalse(response.isStatus());
    }

    @Test
    void approveBlog_success() {

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));

        ResponseDTO response = blogServices.approveBlog("1");

        assertTrue(response.isStatus());
        assertEquals(ApprovalStatus.APPROVED, blog.getApprovalStatus());
    }

    @Test
    void approveBlog_notFound() {

        when(blogRepo.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> blogServices.approveBlog("1"));
    }

    @Test
    void declineBlog_success() {

        when(blogRepo.findById("1")).thenReturn(Optional.of(blog));

        ResponseDTO response = blogServices.declineBlog("1");

        assertTrue(response.isStatus());
        assertEquals(ApprovalStatus.DECLINED, blog.getApprovalStatus());
    }

    @Test
    void declineBlog_notFound() {

        when(blogRepo.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> blogServices.declineBlog("1"));
    }
}