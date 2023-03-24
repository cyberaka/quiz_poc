package com.cyberaka.quiz.dto;

import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.dto.common.QuestionParser;

import javax.persistence.Column;
import java.util.Arrays;
import java.util.List;

public class QuestionDto {
    private Integer questionId;
    private String question;
    private List<String> answers;
    private List<String> options;
    private Integer difficultyLevel;
    private Integer topicId;
    private Integer subTopicId;
    private String explanation;
    private String chapter;
    private String page;
    private String book;

    public QuestionDto() {
        super();
    }

    public void clone(Question question) {
        if (question != null) {
            this.questionId = question.getQuestionId();
            this.question = question.getQuestion();
            this.difficultyLevel = question.getDifficultyLevel();
            this.explanation = question.getExplanation();
            this.chapter = question.getChapter();
            this.page = question.getPage();
            this.book = question.getBook();
            QuestionParser qp = new QuestionParser();
            if (question.getAnswers() != null) {
                this.answers = Arrays.asList(qp.parseAnswers(question.getAnswers()));
//                this.answers = new ArrayList<String>();
//                String answers = question.getAnswers();
//                StringTokenizer st = new StringTokenizer(answers, ",");
//                while (st.hasMoreTokens()) {
//                    this.answers.add(st.nextToken());
//                }
            }
            if (question.getOptions() != null) {
                this.options = Arrays.asList(qp.parseOptions(question.getOptions()));
//                this.options = new ArrayList<String>();
//                String options = question.getOptions();
//                StringTokenizer st = new StringTokenizer(options, "\n");
//                while (st.hasMoreTokens()) {
//                    this.options.add(st.nextToken());
//                }
            }
            if (question.getTopic() != null) {
                this.topicId = question.getTopic().getTopicId();
            }
            if (question.getSubTopic() != null) {
                this.subTopicId = question.getSubTopic().getSubTopicId();
            }
        }
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getSubTopicId() {
        return subTopicId;
    }

    public void setSubTopicId(Integer subTopicId) {
        this.subTopicId = subTopicId;
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

    @Override
    public String toString() {
        return "QuestionDto{" +
                "questionId=" + questionId +
                ", question='" + question + '\'' +
                ", answers=" + answers +
                ", options=" + options +
                ", difficultyLevel=" + difficultyLevel +
                ", topicId=" + topicId +
                ", subTopicId=" + subTopicId +
                ", explanation='" + explanation + '\'' +
                ", chapter='" + chapter + '\'' +
                ", page='" + page + '\'' +
                ", book='" + book + '\'' +
                '}';
    }
}
