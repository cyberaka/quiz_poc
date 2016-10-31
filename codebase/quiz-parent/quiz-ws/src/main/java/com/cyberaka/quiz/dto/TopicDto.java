package com.cyberaka.quiz.dto;

import com.cyberaka.quiz.domain.Topic;

public class TopicDto {
    private Integer topicId;

    private String title;

    public TopicDto() {
        super();

    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void clone(Topic topic) {
        if (topic != null) {
            this.topicId = topic.getTopicId();
            this.title = topic.getTitle();
        }
    }

}
