package com.quizbot.quizbot_springboot_server.services;

import com.quizbot.quizbot_springboot_server.dto.DescribeDto;
import com.quizbot.quizbot_springboot_server.dto.QuizQuestionDTO;
import com.quizbot.quizbot_springboot_server.model.Describe;
import com.quizbot.quizbot_springboot_server.model.Quiz;
import com.quizbot.quizbot_springboot_server.repository.DescribeRepo;
import com.quizbot.quizbot_springboot_server.repository.QuizRepo;
import com.quizbot.quizbot_springboot_server.service.DeepSeekService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeepSeekServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private QuizRepo quizRepo;

    @Mock
    private DescribeRepo describeRepo;

    @InjectMocks
    private DeepSeekService deepSeekService;

    private final String mockQuizResponse = """
            {
              "choices": [
                {
                  "message": {
                    "content": "[{\\"question\\":\\"What is Java?\\",\\"options\\":[\\"Lang\\",\\"Car\\",\\"Animal\\",\\"City\\"],\\"answer\\":\\"Lang\\"}]"
                  }
                }
              ]
            }
            """;

    private final String mockDescribeResponse = """
            {
              "choices": [
                {
                  "message": {
                    "content": "[\\"Java is a programming language.\\",\\"It is widely used.\\"]"
                  }
                }
              ]
            }
            """;

    @BeforeEach
    void setup() throws Exception {
        injectField("apiKey", "fake-api-key");
        injectField("model",  "test-model");
    }

    private void injectField(String fieldName, String value) throws Exception {
        Field f = DeepSeekService.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(deepSeekService, value);
    }


    @Test
    void generateQuizAndSave_success() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockQuizResponse));

        when(quizRepo.save(any(Quiz.class))).thenAnswer(invocation -> {
            Quiz q = invocation.getArgument(0);
            q.setId(1L);
            return q;
        });

        List<QuizQuestionDTO> result =
                deepSeekService.generateQuizAndSave("Java", "Easy", 1, 10L);

        assertEquals(1, result.size());
        assertEquals("What is Java?", result.get(0).getQuestion());
        assertEquals("Lang",          result.get(0).getAnswer());
        assertEquals(4,               result.get(0).getOptions().size());
        verify(quizRepo, times(1)).save(any(Quiz.class));
    }


    @Test
    void generateQuizAndSave_invalidJson_shouldThrow() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("this is not valid json at all"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.generateQuizAndSave("Java", "Easy", 1, 1L));

        assertTrue(ex.getMessage().contains("Failed to generate quiz"),
                "Expected outer message 'Failed to generate quiz' but got: " + ex.getMessage());

        assertNotNull(ex.getCause(), "Expected a cause exception");
        assertTrue(ex.getCause().getMessage().contains("Failed to parse DeepSeek response"),
                "Expected cause message 'Failed to parse DeepSeek response' but got: "
                        + ex.getCause().getMessage());
    }

    @Test
    void generateQuizAndSave_apiReturns404_shouldThrow() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "No endpoint"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.generateQuizAndSave("Java", "Easy", 1, 10L));

        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("Failed to call DeepSeek API"));
    }

    @Test
    void generateQuizAndSave_emptyBody_shouldThrow() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(""));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.generateQuizAndSave("Java", "Easy", 1, 10L));

        assertNotNull(ex.getMessage());
    }

    @Test
    void generateQuizAndSave_apiErrorField_shouldThrow() {
        String errorResponse = """
                {
                  "error": { "message": "rate limit exceeded" }
                }
                """;

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(errorResponse));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.generateQuizAndSave("Java", "Easy", 1, 10L));

        assertNotNull(ex.getMessage());
    }

    @Test
    void submitAnswer_correctAnswer() {
        Quiz quiz = buildQuiz(1L, "What is Java?", "Lang");

        when(quizRepo.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        QuizQuestionDTO result = deepSeekService.submitAnswer(1L, "Lang");

        assertTrue(result.isCorrect());
        assertEquals("Lang", result.getUserAnswer());
        assertEquals("Lang", result.getAnswer());
        assertEquals("What is Java?", result.getQuestion());
        assertEquals(4, result.getOptions().size());
    }

    @Test
    void submitAnswer_wrongAnswer() {
        Quiz quiz = buildQuiz(1L, "What is Java?", "Lang");

        when(quizRepo.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        QuizQuestionDTO result = deepSeekService.submitAnswer(1L, "Car");

        assertFalse(result.isCorrect());
        assertEquals("Car", result.getUserAnswer());
    }

    @Test
    void submitAnswer_quizNotFound() {
        when(quizRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.submitAnswer(1L, "Lang"));

        assertNotNull(ex.getMessage());
        assertTrue(ex.getMessage().contains("Quiz not found"));
    }


    @Test
    void generateDescribeAndSave_success() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockDescribeResponse));

        when(describeRepo.save(any(Describe.class))).thenAnswer(i -> i.getArgument(0));

        List<DescribeDto> result = deepSeekService.generateDescribeAndSave("Java", 10L);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getDescription().contains("Java"));
        assertEquals("Java", result.get(0).getTopic());
        assertEquals(10L,    result.get(0).getUserid());
        verify(describeRepo, times(1)).save(any(Describe.class));
    }


    @Test
    void generateDescribeAndSave_invalidJson_shouldThrow() {

        String badContentResponse = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "this is not a json array"
                      }
                    }
                  ]
                }
                """;

        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(badContentResponse));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.generateDescribeAndSave("Java", 1L));

        assertTrue(ex.getMessage().contains("Failed to generate description"),
                "Expected outer message 'Failed to generate description' but got: "
                        + ex.getMessage());

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Failed to parse DeepSeek description response"),
                "Expected cause 'Failed to parse DeepSeek description response' but got: "
                        + ex.getCause().getMessage());
    }

    @Test
    void generateDescribeAndSave_apiReturns401_shouldThrow() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deepSeekService.generateDescribeAndSave("Java", 1L));

        assertTrue(ex.getMessage().contains("Failed to call DeepSeek API"));
    }


    private Quiz buildQuiz(Long id, String question, String correctAnswer) {
        Quiz q = new Quiz();
        q.setId(id);
        q.setQuestion(question);
        q.setOptions("[\"Lang\",\"Car\",\"Animal\",\"City\"]");
        q.setCorrectAnswer(correctAnswer);
        return q;
    }
}