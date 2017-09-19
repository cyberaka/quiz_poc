package com.cyberaka.quiz.dto;

/**
 * Created by 562320 on 9/20/17.
 */
public class CandidateResultDto {

    private int questionNumber;

    private String question;

    private String candidateAnswer;

    private String correct;

    public CandidateResultDto() {
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCandidateAnswer() {
        return candidateAnswer;
    }

    public void setCandidateAnswer(String candidateAnswer) {
        this.candidateAnswer = candidateAnswer;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    @Override
    public String toString() {
        return "CandidateResultDto{" +
                "questionNumber=" + questionNumber +
                ", question='" + question + '\'' +
                ", candidateAnswer='" + candidateAnswer + '\'' +
                ", correct='" + correct + '\'' +
                '}';
    }
}
