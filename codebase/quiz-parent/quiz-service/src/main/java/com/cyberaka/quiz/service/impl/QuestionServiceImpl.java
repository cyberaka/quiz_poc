package com.cyberaka.quiz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.dao.UserRepository;
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
	
	@Override
	public void bootup() {
		User user = new User();
		user.setAdmin(true);
		user.setConsumer(true);
		user.setPublisher(true);
		user.setEmail("cyberaka@gmail.com");
		user.setPassword("1234");
		user.setPhoneNo("1234");
		user.setUserName("cyberaka");
		user.setName("Abhinav Anand");
		userRepo.save(user);
		
		Topic topic = new Topic();
		topic.setTitle("Java");
		topicRepo.save(topic);
		
		SubTopic subTopic = new SubTopic();
		subTopic.setTitle("Section 1");
		subtopicRepo.save(subTopic);
	}

}
