package com.cyberaka.quiz.domain;

public class Question {
	private Integer questionId;
	private String question;
	private String answers;
	private String options;
	private Topic topic;
	private SubTopic subTopic;
	private User contributer;
	
	public Question() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswers() {
		return answers;
	}
	public void setAnswers(String answers) {
		this.answers = answers;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	public SubTopic getSubTopic() {
		return subTopic;
	}
	public void setSubTopic(SubTopic subTopic) {
		this.subTopic = subTopic;
	}
	public User getContributer() {
		return contributer;
	}
	public void setContributer(User contributer) {
		this.contributer = contributer;
	}
	
}
