package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.PdfGenaratorUtil;
import com.cyberaka.quiz.common.QuizAppConstants;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by cyberaka on 10/16/17.
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class QuestionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private SubTopicRepository subTopicRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    PdfGenaratorUtil pdfGenaratorUtil;

    @Before
    public void setUp() throws Exception {
        User user = new User();
        user.setAdmin(true);
        user.setConsumer(false);
        user.setEmail("cyberaka@gmail.com");
        user.setName("Abhinav Anand");
        user.setPassword("abcd1234");
        user.setPhoneNo("1234567890");
        user.setPublisher(false);
        user.setUserName("cyberaka");
        entityManager.persist(user);
        entityManager.flush();

        Topic topic = new Topic();
        topic.setTitle("EE SPOT ERROR 1");
        entityManager.persist(topic);
        entityManager.flush();

        SubTopic subTopic = new SubTopic();
        subTopic.setTopic(topic);
        subTopic.setTitle("All");
        entityManager.persist(subTopic);
        entityManager.flush();
    }

    // write test cases here
    @Test
    public void whenFindByName_thenReturnTopic() {
        User user = userRepository.findById(1).get();
        SubTopic subtopic = subTopicRepository.findById(1).get();
        Topic topic = subtopic.getTopic();

        // given
        Question question = new Question();
        question.setQuestion("EE 185  1.  Find the error:-    Magic realism is one of the latest addition to good literature published in recent times . ");
        question.setOptions("A. Magic realism is one of\n" +
                "B. the latest addition\n" +
                "C. to good literature\n" +
                "D. published in recent times\n" +
                "E. No error");
        question.setAnswers("B");
        question.setDifficultyLevel(QuizAppConstants.DIFFICULTY_MEDIUM);
        question.setContributer(user);
        question.setTopic(topic);
        question.setSubTopic(subtopic);
        entityManager.persist(question);
        entityManager.flush();

        // when
        Question foundQuestion = questionRepository.findById(1).get();

        // then
        assertThat(foundQuestion.getQuestion())
            .isEqualTo(question.getQuestion());

    }
}
