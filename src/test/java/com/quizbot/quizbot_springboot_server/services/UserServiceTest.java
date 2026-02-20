package com.quizbot.quizbot_springboot_server.services;

import com.quizbot.quizbot_springboot_server.dto.*;
import com.quizbot.quizbot_springboot_server.model.*;
import com.quizbot.quizbot_springboot_server.repository.*;
import com.quizbot.quizbot_springboot_server.security.jwt.JWTService;
import com.quizbot.quizbot_springboot_server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTService jwtService;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private QuizRepo quizRepo;

    @Mock
    private DescribeRepo describeRepo;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@mail.com");
        user.setPassword("password");
        user.setName("John");
        user.setJoined_date(LocalDate.now().minusDays(10));
    }


    @Test
    void createUser_success() {

        Role role = new Role();
        role.setName(RoleType.ROLE_USER);

        when(userRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(roleRepo.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(role));
        when(userRepo.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.createUser(user);

        assertNotNull(saved);
        assertEquals("encodedPass", saved.getPassword());
        verify(userRepo).save(user);
    }

    @Test
    void createUser_emailAlreadyExists() {

        when(userRepo.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> userService.createUser(user));
    }

    @Test
    void createUser_roleNotFound() {

        when(userRepo.existsByEmail(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(roleRepo.findByName(RoleType.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.createUser(user));
    }


    @Test
    void generateToken_success() {

        when(jwtService.generateToken(anyString(), anyLong(), any(User.class)))
                .thenReturn("jwt-token");

        String token = userService.generateToken(user.getEmail(), 1L, user);

        assertEquals("jwt-token", token);
    }

    @Test
    void login_success() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@mail.com");
        dto.setPassword("password");

        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", user.getPassword())).thenReturn(true);

        User loggedUser = userService.logIn(dto);

        assertEquals(user, loggedUser);
    }

    @Test
    void login_invalidCredentials() {

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("test@mail.com");
        dto.setPassword("wrong");

        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.logIn(dto));
    }


    @Test
    void getUserFromJWTToken_success() {

        when(jwtService.extractEmail("token")).thenReturn(user.getEmail());
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDTO.class))
                .thenReturn(new UserResponseDTO());

        UserResponseDTO dto = userService.getUserFromJWTToken("token");

        assertNotNull(dto);
    }

    @Test
    void getUserFromJWTToken_invalidToken() {

        when(jwtService.extractEmail("token")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> userService.getUserFromJWTToken("token"));
    }

    @Test
    void getUserFromJWTToken_userNotFound() {

        when(jwtService.extractEmail("token")).thenReturn(user.getEmail());
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getUserFromJWTToken("token"));
    }


    @Test
    void getUserPreviousQuestions_success() {

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(quizRepo.findByUserId(user.getId())).thenReturn(List.of(new Quiz()));

        List<Quiz> quizzes =
                userService.getUserPreviousQuestions(user.getEmail());

        assertEquals(1, quizzes.size());
    }

    @Test
    void getUserPreviousQuestions_userNotFound() {

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getUserPreviousQuestions(user.getEmail()));
    }


    @Test
    void getUserStats_success() {

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(quizRepo.countByUserId(user.getId())).thenReturn(5L);
        when(describeRepo.countByUserId(user.getId())).thenReturn(3L);

        UserStatsDTO stats = userService.getUserStats(user.getEmail());

        assertEquals(5L, stats.getQuestionsCovered());
        assertEquals(3L, stats.getTopicsCovered());
        assertEquals(user.getName(), stats.getName());
        assertTrue(stats.getDaysLogged() >= 10);
    }

    @Test
    void getUserStats_userNotFound() {

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getUserStats(user.getEmail()));
    }
}