package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends CrudRepository<Question, Integer> {
    @Query("from Question a where a.topic.topicId =:topicId and a.subTopic.subTopicId=:subTopicId and a.difficultyLevel=:level")
    public Iterable<Question> findQuiz(@Param("topicId") int topicId, @Param("subTopicId") int subTopicId, @Param("level") int level);

    @Query("from Question a where a.topic.topicId =:topicId and a.subTopic.subTopicId=:subTopicId")
    public Iterable<Question> findQuiz(@Param("topicId") int topicId, @Param("subTopicId") int subTopicId);
}
