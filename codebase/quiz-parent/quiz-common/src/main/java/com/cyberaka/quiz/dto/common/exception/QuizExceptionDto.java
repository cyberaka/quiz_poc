package com.cyberaka.quiz.dto.common.exception;

/**
 * Created by Abhinav on 9/17/17.
 */
public class QuizExceptionDto {
    long timestamp;
    int staus;
    String message;

    public QuizExceptionDto() {
        this.timestamp = System.currentTimeMillis();
    }

    public QuizExceptionDto(int staus, String message) {
        this.timestamp = System.currentTimeMillis();
        this.staus = staus;
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStaus() {
        return staus;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "QuizExceptionDto{" +
                "timestamp=" + timestamp +
                ", staus=" + staus +
                ", message='" + message + '\'' +
                '}';
    }
}
