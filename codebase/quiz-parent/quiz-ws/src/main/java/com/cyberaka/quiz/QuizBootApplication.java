package com.cyberaka.quiz;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entry point into the quiz boot application.
 * 
 * @author anindita
 *
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cyberaka.quiz.dao")
@EntityScan(basePackages = "com.cyberaka.quiz.domain")
@ComponentScan(basePackages = { "com.cyberaka.quiz", "com.cyberaka.quiz.rest", "com.cyberaka.quiz.dao", "com.cyberaka.quiz.service.impl" })
public class QuizBootApplication {
	Logger log = Logger.getLogger(getClass().getName());
	
	public QuizBootApplication() {
		// Default constructor.
	}
	
	public void info() {
		log.info("Quiz Boot Applicaiton");
	}
	public static void main(String[] args) {
		SpringApplication.run(QuizBootApplication.class, args);
	}
}
