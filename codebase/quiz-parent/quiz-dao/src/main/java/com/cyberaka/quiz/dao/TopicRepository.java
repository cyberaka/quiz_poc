package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.Topic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TopicRepository extends CrudRepository<Topic, Integer> {

    List<Topic> findByTitle(String title);

}
