package com.cyberaka.quiz.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_sub_topic")
public class SubTopic {
	@Id
	@Column(name = "sub_topic_id", nullable = false)

	private Integer subTopicId;
	@Column(name = "title")
	private String title;

	public SubTopic() {
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

}
