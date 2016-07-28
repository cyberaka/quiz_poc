package com.cyberaka.quiz.domain;

public class Topic {
	private Integer topicId;
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
