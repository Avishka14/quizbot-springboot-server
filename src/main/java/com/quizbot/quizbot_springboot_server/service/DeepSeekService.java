package com.quizbot.quizbot_springboot_server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizbot.quizbot_springboot_server.dto.DescribeDto;
import com.quizbot.quizbot_springboot_server.dto.QuizQuestionDTO;
import com.quizbot.quizbot_springboot_server.model.Describe;
import com.quizbot.quizbot_springboot_server.model.Quiz;
import com.quizbot.quizbot_springboot_server.repository.DescribeRepo;
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
    private final QuizRepo quizRepo;
    private final DescribeRepo describeRepo;

    @Value("${deepseek.api.key}")
    private String apiKey;

    public DeepSeekService(QuizRepo quizRepo, DescribeRepo describeRepo) {
        this.restTemplate = new RestTemplate();
        this.quizRepo = quizRepo;
        this.describeRepo = describeRepo;
    }

    public List<QuizQuestionDTO> generateQuizAndSave(String topic , String difficulty , int count , String userId) {
        String prompt = "Generate" +count+  "multiple-choice quiz questions on: " + topic +
                ". in knowledge level" + difficulty + " Each question should have 'question', 'options' (array of 4), and 'answer' fields. Format your response as plain JSON array only. Do not include explanations or markdown.";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://openrouter.ai/api/v1/chat/completions";

        Map<String, Object> body = Map.of(
                "model", "tngtech/deepseek-r1t2-chimera:free",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        List<QuizQuestionDTO> dtos = extractQuizListFromResponse(response.getBody());
        ObjectMapper objectMapper = new ObjectMapper();

        List<Quiz> savedQuestions = dtos.stream()
                .map(dto -> {
                    try {
                    Quiz q = new Quiz();
                    q.setUserId(userId);
                    q.setTopic(topic);
                    q.setQuestion(dto.getQuestion());
                    q.setOptions(objectMapper.writeValueAsString(dto.getOptions()));
                    q.setCorrectAnswer(dto.getAnswer());
                    q.setUserAnswer(null);
                    q.setCorrect(false);
                    return quizRepo.save(q);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        return savedQuestions.stream()
                .map(q -> {
                    try {
                        List<String> options = objectMapper.readValue(q.getOptions(), List.class);

                        QuizQuestionDTO dto = new QuizQuestionDTO();
                        dto.setId(q.getId());
                        dto.setQuestion(q.getQuestion());
                        dto.setOptions(options);
                        dto.setAnswer(q.getCorrectAnswer());
                        dto.setUserAnswer(q.getUserAnswer());
                        dto.setCorrect(q.isCorrect());

                        return dto;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

    }

    public QuizQuestionDTO submitAnswer(Long quizId, String userAnswer) {
        Quiz quiz = quizRepo.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setUserAnswer(userAnswer);
        quiz.setCorrect(userAnswer.equals(quiz.getCorrectAnswer()));

        Quiz saved = quizRepo.save(quiz);

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<String> options = mapper.readValue(saved.getOptions(), List.class);

            QuizQuestionDTO dto = new QuizQuestionDTO();
            dto.setId(saved.getId());
            dto.setQuestion(saved.getQuestion());
            dto.setOptions(options);
            dto.setAnswer(saved.getCorrectAnswer());
            dto.setUserAnswer(saved.getUserAnswer());
            dto.setCorrect(saved.isCorrect());

            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<QuizQuestionDTO> extractQuizListFromResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            String content = root.path("choices").get(0).path("message").path("content").asText();

            content = content.replaceAll("(?s)```json|```", "").trim();

            int start = content.indexOf('[');
            int end = content.lastIndexOf(']');
            if (start == -1 || end == -1 || end <= start) {
                throw new RuntimeException("No JSON array found in the AI response");
            }

            String jsonArray = content.substring(start, end + 1);

            return Arrays.asList(mapper.readValue(jsonArray, QuizQuestionDTO[].class));
        } catch (Exception e) {
            System.err.println("Raw DeepSeek response:\n" + json);
            throw new RuntimeException("Failed to parse DeepSeek response", e);
        }
    }


    public List<DescribeDto> generateDescribeAndSave(String topic , String userId){

        String prompt = "Generate a short description about " + topic +
                ". Use around 150 to 200 words. Format your response as a plain JSON array" +
                " Do not include explanations, headers, or markdown.";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "https://openrouter.ai/api/v1/chat/completions";

        Map<String, Object> body = Map.of(
                "model", "tngtech/deepseek-r1t2-chimera:free",
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        List<DescribeDto> dtos = extractDescriptionListFromResponse(response.getBody());

        Describe d = new Describe();
        d.setTopic(topic);
        d.setUserid(userId);
        describeRepo.save(d);

        return dtos.stream().peek(dto -> {
            dto.setTopic(topic);
            dto.setUserid(userId);
        }).toList();


    }

    private List<DescribeDto> extractDescriptionListFromResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            String content = root.path("choices").get(0).path("message").path("content").asText();

            if (content.startsWith("```")) {
                content = content.replaceAll("(?s)^```(?:json)?\\s*|\\s*```$", "");
            }

            List<String> sentences = Arrays.asList(mapper.readValue(content, String[].class));

            String fullDescription = String.join(" ", sentences);

            DescribeDto dto = new DescribeDto();
            dto.setDescription(fullDescription);

            return List.of(dto);

        } catch (Exception e) {
            System.err.println("Raw DeepSeek Describe response:\n" + json);
            throw new RuntimeException("Failed to parse DeepSeek description response", e);
        }
    }




}
