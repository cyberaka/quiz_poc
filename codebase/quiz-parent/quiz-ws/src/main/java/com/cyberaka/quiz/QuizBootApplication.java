package com.cyberaka.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.logging.Logger;

/**
 * Entry point into the quiz boot application.
 *
 * @author anindita
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.cyberaka.quiz.dao")
@EntityScan(basePackages = "com.cyberaka.quiz.domain")
@ComponentScan(basePackages = {"com.cyberaka.quiz", "com.cyberaka.quiz.rest", "com.cyberaka.quiz.dao", "com.cyberaka.quiz.service.impl"})
public class QuizBootApplication {

    static final Logger LOG = Logger.getLogger(QuizBootApplication.class.getName());

    public QuizBootApplication() {
        // Default constructor.
    }

    public static void main(String[] args) {
        SpringApplication.run(QuizBootApplication.class, args);
    }

    public void info() {
        LOG.info("Quiz Boot Applicaiton");
    }
}
