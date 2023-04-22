package com.cyberaka.quiz.dto;

import com.cyberaka.quiz.domain.SubTopic;

public class SubTopicDto {

    private String subTopicId;
    private String title;
    private String topicId;

    public SubTopicDto() {
        super();
    }

    public String getSubTopicId() {
        return subTopicId;
    }

    public void setSubTopicId(String subTopicId) {
        this.subTopicId = subTopicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void clone(SubTopic subTopic) {
        if (subTopic != null) {
            this.subTopicId = subTopic.getSubTopicId();
            this.title = subTopic.getTitle();
            if (subTopic.getTopic() != null) {
                this.topicId = subTopic.getTopic().getTopicId();
            }
        }
    }

}
