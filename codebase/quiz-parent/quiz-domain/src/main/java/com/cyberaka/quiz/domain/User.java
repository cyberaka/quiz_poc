package com.cyberaka.quiz.domain;

import javax.persistence.*;

@Entity
@Table(name = "quiz_user", uniqueConstraints = @UniqueConstraint(name="unique_user_id", columnNames = {"user_name"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Column(name = "user_name", nullable = false, unique =  true)
    private String userName;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "phone_no", nullable = false)
    private String phoneNo;
    @Column(name = "admin", nullable = false)
    private boolean admin;
    @Column(name = "publisher", nullable = false)
    private boolean publisher;
    @Column(name = "consumer", nullable = false)
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
