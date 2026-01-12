package com.quizbot.quizbot_springboot_server.utility;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class HelperMethods {

    private final String uploadDir = "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Logger logger = LoggerFactory.getLogger(HelperMethods.class);


    public ResponseDTO validateBlogInput(BlogDto blogDto, MultipartFile file, boolean fileRequired) {

        if (blogDto.getTitle() == null || blogDto.getTitle().isBlank()) {
            return new ResponseDTO(false, "Title cannot be empty");
        }

        if (blogDto.getTitle().length() > 100) {
            return new ResponseDTO(false, "Title must be 100 characters or less");
        }

        if (blogDto.getCategory() == null || blogDto.getCategory().isBlank()) {
            return new ResponseDTO(false, "Category cannot be empty");
        }


        if (blogDto.getDescription() == null || blogDto.getDescription().isBlank()) {
            return new ResponseDTO(false, "Description cannot be empty");
        }

        if (fileRequired && (file == null || file.isEmpty())) {
            return new ResponseDTO(false, "Cover image is required");
        }

        if (file != null && !file.isEmpty()) {
            if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
                return new ResponseDTO(false, "Only image files are allowed");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return new ResponseDTO(false, "Image size must be less than 5MB");
            }
        }

        return null;
    }

    public void deleteFileIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
            logger.info("Deleted temporary file: {}", path);
        } catch (IOException e) {
            logger.error("Failed to delete temporary file: {}", path, e);
        }
    }

    public void deleteOldImageFile(String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            try {
                String fileName = imageUrl.replace("/files/", "");
                Path oldPath = Paths.get(uploadDir, fileName);
                Files.deleteIfExists(oldPath);
                logger.info("Deleted old image file: {}", fileName);
            } catch (IOException e) {
                logger.error("Failed to delete old image file: {}", imageUrl, e);
            }
        }
    }
}
