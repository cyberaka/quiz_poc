package com.cyberaka.quiz.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("questions_option")
public class QuestionOption {

    private String option;

    private String text;

    public QuestionOption() {
    }

    public QuestionOption(String option, String text) {
        this.option = option;
        this.text = text;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "QuestionOption{" +
                "option='" + option + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
