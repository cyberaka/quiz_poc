package com.cyberaka.quiz.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("sub_topics")
public class SubTopic {
    @MongoId
    private String subTopicId;
    private String title;
    private Topic topic;

    public SubTopic() {
        super();

    }

    public String getSubTopicId() {
        return subTopicId;
    }

    public void setSubTopicId(String subTopicId) {
        this.subTopicId = subTopicId;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
