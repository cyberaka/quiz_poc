package com.cyberaka.quiz.domain;

public class User {
	private Integer userId;
	private String userName;
	private String password;
	private String email;
	private String phoneNo;
	private boolean admin;
	private boolean publisher;
	private boolean consumer;

	public User() {
		super();
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isPublisher() {
		return publisher;
	}

	public void setPublisher(boolean publisher) {
		this.publisher = publisher;
	}

	public boolean isConsumer() {
		return consumer;
	}

	public void setConsumer(boolean consumer) {
		this.consumer = consumer;
	}

}
