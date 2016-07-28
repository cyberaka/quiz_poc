package com.cyberaka.quiz.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.cyberaka.quiz.domain.SubTopic;

public interface SubTopicRepository extends CrudRepository<SubTopic, Integer> {
	@Query("from SubTopic a where a.topic.topicId =:topicId")
	public Iterable<SubTopic> findByTopic(@Param("topicId") Integer topicId);
}
