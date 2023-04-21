package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.SubTopic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubTopicRepository extends MongoRepository<SubTopic, String> {
    public List<SubTopic> findByTopic(Integer topic);
}

//public interface SubTopicRepository extends CrudRepository<SubTopic, Integer> {
//    @Query("from SubTopic a where a.topic.topicId =:topicId")
//    public Iterable<SubTopic> findByTopic(@Param("topicId") Integer topicId);
//}
