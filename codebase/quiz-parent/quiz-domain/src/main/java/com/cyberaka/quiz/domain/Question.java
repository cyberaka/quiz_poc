package com.cyberaka.quiz.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Arrays;


@Document("questions")
public class Question {
    @MongoId
    private String questionId;
    private String question;
    private String answers;
    private QuestionOption[] options;
    private int difficultyLevel;
    private String explanation;
    private String chapter;
    private String page;
    private String book;
    private Topic topic;
    private SubTopic subTopic;
    private User contributer;

    public Question() {
        super();
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public QuestionOption[] getOptions() {
        return options;
    }

    public void setOptions(QuestionOption[] options) {
        this.options = options;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public SubTopic getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(SubTopic subTopic) {
        this.subTopic = subTopic;
    }

    public User getContributer() {
        return contributer;
    }

    public void setContributer(User contributer) {
        this.contributer = contributer;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public boolean isValid() {
        return topic != null && subTopic != null && contributer != null &&
                options != null && options != null &&
                answers != null && !answers.isEmpty() && question != null &&
                !question.isEmpty();
    }

    public boolean isValidV2() {
        return topic != null && subTopic != null && contributer != null &&
                options != null &&
                answers != null && question != null &&
                !question.isEmpty() &&
                explanation != null &&
                chapter != null && !chapter.isEmpty() &&
                page != null && !page.isEmpty() &&
                book != null && !book.isEmpty();
    }

    @Override
    public String toString() {
        return "Question{" +
                ", question='" + question + '\'' +
                ", answers='" + answers + '\'' +
                ", options=" + Arrays.toString(options) +
                ", difficultyLevel=" + difficultyLevel +
                ", explanation='" + explanation + '\'' +
                ", chapter='" + chapter + '\'' +
                ", page='" + page + '\'' +
                ", book='" + book + '\'' +
                ", topic=" + topic.getTitle() +
                ", subTopic=" + subTopic.getTitle() +
                '}';
    }
}
