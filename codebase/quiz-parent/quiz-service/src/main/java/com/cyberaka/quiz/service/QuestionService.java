package com.cyberaka.quiz.service;

import com.cyberaka.quiz.domain.Question;

public interface QuestionService {
    public Iterable<Question> findQuiz(String topicId, String subTopicId, int level, int count);
}
