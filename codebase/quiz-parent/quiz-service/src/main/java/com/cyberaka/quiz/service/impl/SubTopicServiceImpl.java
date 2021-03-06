package com.cyberaka.quiz.service.impl;

import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.service.SubTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubTopicServiceImpl implements SubTopicService {
    @Autowired
    SubTopicRepository subTopicRepo;

    public Iterable<SubTopic> findByTopic(Integer topicId) {
        return subTopicRepo.findByTopic(topicId);

    }
}
