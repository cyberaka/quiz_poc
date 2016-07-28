package com.cyberaka.quiz.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.dao.UserRepository;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepository questionRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	TopicRepository topicRepo;
	
	@Autowired
	SubTopicRepository subtopicRepo;

}
