package com.cyberaka.quiz.dto;

import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.QuestionOption;
import com.cyberaka.quiz.dto.common.QuestionParser;

import java.util.Arrays;
import java.util.List;

public class QuestionDto {
    private String questionId;
    private String question;
    private List<String> answers;
    private List<QuestionOption> options;
    private Integer difficultyLevel;
    private String topicId;
    private String subTopicId;
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
                //this.options = Arrays.asList(qp.parseOptions(question.getOptions()));
                this.options = Arrays.asList(question.getOptions());
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

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<QuestionOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOption> options) {
        this.options = options;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getSubTopicId() {
        return subTopicId;
    }

    public void setSubTopicId(String subTopicId) {
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
