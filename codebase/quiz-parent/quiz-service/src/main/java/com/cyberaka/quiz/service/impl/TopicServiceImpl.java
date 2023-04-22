package com.cyberaka.quiz.service.impl;

import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    TopicRepository topicRepo;

    public Iterable<Topic> findAll() {
        return topicRepo.findAll();

    }

    @Override
    public Topic findTopic(String topicId) {
        return topicRepo.findById(topicId).get();
    }
}
