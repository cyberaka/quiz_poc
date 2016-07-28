package com.cyberaka.quiz.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_sub_topic")
public class SubTopic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "sub_topic_id", nullable = false)

	private Integer subTopicId;
	@Column(name = "title")
	private String title;

	@ManyToOne
	@JoinColumn(name = "topic_id", referencedColumnName = "topic_id")
	private Topic topic;

	public SubTopic() {
		super();

	}

	public Integer getSubTopicId() {
		return subTopicId;
	}

	public void setSubTopicId(Integer subTopicId) {
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
