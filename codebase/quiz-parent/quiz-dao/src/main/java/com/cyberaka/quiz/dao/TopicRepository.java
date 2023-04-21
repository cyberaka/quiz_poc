package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TopicRepository extends MongoRepository<Topic, String> {
    List<Topic> findByTitle(String title);
}

//public interface TopicRepository extends CrudRepository<Topic, Integer> {
//
//    List<Topic> findByTitle(String title);
//
//}
