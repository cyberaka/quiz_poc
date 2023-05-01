package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {
//    @Query("{name:'?0'}")
//    GroceryItem findItemByName(String name);
//
//    @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
//    List<GroceryItem> findAll(String category);
//
//    public List<Question> findByTopicAndSubTopicAndLevel(Integer topic, Integer subTopic, Integer level);

    public List<Question> findByTopicAndSubTopic(Topic topic, SubTopic subTopic);

    @Query("{\"topic.title\": '?0', \"subTopic.title\": '?1'}")
    public List<Question> queryByTopicAndSubTopic(String topic, String subTopic);

    @Query("{\"question\" : '?0', \"topic.title\": '?1', \"subTopic.title\": '?2'}")
    public List<Question> findByQuestionAndTopicAndSubTopic(String question, String topic, String subTopic);

//    public long count();
}

//    private Topic topic;
//    private SubTopic subTopic;


//public interface QuestionRepository extends CrudRepository<Question, Integer> {
//    @Query("from Question a where a.topic.topicId =:topicId and a.subTopic.subTopicId=:subTopicId and a.difficultyLevel=:level")
//    public Iterable<Question> findQuiz(@Param("topicId") int topicId, @Param("subTopicId") int subTopicId, @Param("level") int level);
//
//    @Query("from Question a where a.topic.topicId =:topicId and a.subTopic.subTopicId=:subTopicId")
//    public Iterable<Question> findQuiz(@Param("topicId") int topicId, @Param("subTopicId") int subTopicId);
//}
