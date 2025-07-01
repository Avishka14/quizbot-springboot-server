package com.quizbot.quizbot_springboot_server;

import com.quizbot.quizbot_springboot_server.dto.BlogDto;
import com.quizbot.quizbot_springboot_server.model.Blog;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuizbotSpringbootServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuizbotSpringbootServerApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();

		// Skip setting ID when mapping from BlogDto to Blog
		mapper.typeMap(BlogDto.class, Blog.class).addMappings(
				m -> m.skip(Blog::setId));

		return mapper;
	}

}
