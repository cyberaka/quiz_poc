package com.cyberaka.quiz.dto;

import java.util.List;

/**
 * Created by cyberaka on 9/19/17.
 */
public class QuestionAnswerDto extends QuestionDto {

    private List<String> userAnswers;

    public QuestionAnswerDto() {
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    @Override
    public String toString() {
        return "QuestionAnswerDto{" +
                "questionId=" + super.getQuestionId() +
                ", question='" + super.getQuestion() + '\'' +
                ", answers=" + super.getAnswers() +
                ", options=" + super.getOptions() +
                ", difficultyLevel=" + super.getDifficultyLevel() +
                ", topicId=" + super.getTopicId() +
                ", subTopicId=" + super.getSubTopicId() +
                ", userAnswers=" + userAnswers +
                '}';
    }
}
