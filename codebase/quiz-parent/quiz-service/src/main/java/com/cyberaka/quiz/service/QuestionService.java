package com.cyberaka.quiz.service;

import com.cyberaka.quiz.domain.Question;

public interface QuestionService {
    public Iterable<Question> findQuiz(int topicId, int subTopicId, int level,int count);
}
