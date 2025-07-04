package com.quizbot.quizbot_springboot_server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizbot.quizbot_springboot_server.dto.QuizQuestionDTO;
import com.quizbot.quizbot_springboot_server.model.Quiz;
import com.quizbot.quizbot_springboot_server.repository.QuizRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {

    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;
    private final QuizRepo quizRepo;

    @Value("${deepseek.api.key}")
    private String apiKey;

    public DeepSeekService(QuizRepo quizRepo, ModelMapper modelMapper) {
        this.restTemplate = new RestTemplate();
        this.quizRepo = quizRepo;
        this.modelMapper = modelMapper;
    }

    public List<QuizQuestionDTO> generateQuizAndSave(String topic, String userId) {
        String prompt = "Generate 5 multiple-choice quiz questions on: " + topic +
                ". Each question should have 'question', 'options' (array of 4), and 'answer' fields. Format your response as plain JSON array only. Do not include explanations or markdown.";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://openrouter.ai/api/v1/chat/completions";

        Map<String, Object> body = Map.of(
                "model", "deepseek/deepseek-r1:free",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        List<QuizQuestionDTO> dtos = extractQuizListFromResponse(response.getBody());

        List<Quiz> savedQuestions = dtos.stream()
                .map(dto -> {
                    Quiz q = modelMapper.map(dto, Quiz.class);
                    q.setUserId(userId);
                    q.setTopic(topic);
                    return q;
                })
                .map(quizRepo::save)
                .toList();

        return savedQuestions.stream()
                .map(q -> modelMapper.map(q, QuizQuestionDTO.class))
                .toList();
    }

    private List<QuizQuestionDTO> extractQuizListFromResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String content = root.path("choices").get(0).path("message").path("content").asText();

            // Clean triple backticks (``` or ```json) from LLM-style output
            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)^```(?:json)?\\s*|\\s*```$", "");
            }

            return Arrays.asList(mapper.readValue(content, QuizQuestionDTO[].class));
        } catch (Exception e) {
            System.err.println("Raw DeepSeek response:\n" + json);
            throw new RuntimeException("Failed to parse DeepSeek response", e);
        }
    }
}
