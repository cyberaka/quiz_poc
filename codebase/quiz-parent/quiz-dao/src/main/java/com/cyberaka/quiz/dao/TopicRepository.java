package com.cyberaka.quiz.dao;

import org.springframework.data.repository.CrudRepository;

import com.cyberaka.quiz.domain.Topic;

public interface TopicRepository extends CrudRepository<Topic, Integer> {


}
