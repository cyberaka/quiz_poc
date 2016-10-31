package com.cyberaka.quiz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.service.SubTopicService;

@Service
public class SubTopicServiceImpl implements SubTopicService {
    @Autowired
    SubTopicRepository subTopicRepo;

    public Iterable<SubTopic> findByTopic(Integer topicId) {
        return subTopicRepo.findByTopic(topicId);

    }
}
