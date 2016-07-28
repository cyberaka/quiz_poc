package com.cyberaka.quiz.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_topic")
public class Topic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "topic_id", nullable = false)
	private Integer topicId;

	@Column(name = "title")
	private String title;

	public Topic() {
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
}
