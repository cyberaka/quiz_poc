package com.cyberaka.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cyberaka.quiz.dao")
@EntityScan(basePackages = "com.cyberaka.quiz.domain")
@ComponentScan(basePackages = { "com.cyberaka.quiz.rest", "com.cyberaka.quiz.dao", "com.cyberaka.quiz.service.impl" })
public class QuizBootApplication {
	public static void main(String[] args) {
		SpringApplication.run(QuizBootApplication.class, args);
	}
}
