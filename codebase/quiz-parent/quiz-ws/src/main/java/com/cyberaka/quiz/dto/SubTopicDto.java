package com.cyberaka.quiz.dto;

import com.cyberaka.quiz.domain.SubTopic;

public class SubTopicDto {

    private Integer subTopicId;
    private String title;
    private Integer topicId;

    public SubTopicDto() {
        super();
    }

    public Integer getSubTopicId() {
        return subTopicId;
    }

    public void setSubTopicId(Integer subTopicId) {
        this.subTopicId = subTopicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
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
