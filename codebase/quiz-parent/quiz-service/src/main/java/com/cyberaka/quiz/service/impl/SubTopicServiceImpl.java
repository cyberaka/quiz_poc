package com.cyberaka.quiz.service.impl;

import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.service.SubTopicService;
import com.cyberaka.quiz.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubTopicServiceImpl implements SubTopicService {
    @Autowired
    SubTopicRepository subTopicRepo;

    @Autowired
    TopicService topicService;

    public Iterable<SubTopic> findByTopic(String topicId) {
        Topic topic = topicService.findTopic(topicId);
        return subTopicRepo.findByTopic(topic);
    }

    @Override
    public SubTopic findSubTopic(String subTopicId) {
        return subTopicRepo.findById(subTopicId).get();
    }
}
