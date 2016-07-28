package com.cyberaka.quiz.service;

import com.cyberaka.quiz.domain.SubTopic;

public interface SubTopicService {
	public Iterable<SubTopic> findByTopic(Integer topicId);
}
