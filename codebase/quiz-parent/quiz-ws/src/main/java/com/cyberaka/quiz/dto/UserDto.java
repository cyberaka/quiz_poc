package com.cyberaka.quiz.dto;

import com.cyberaka.quiz.domain.User;

/**
 * User DTO to capture user details.
 */
public class UserDto {
    private String userId;
    private String userName;
    private String name;
    private String email;
    private String phoneNo;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

//    @Override
//    protected UserDto clone() throws CloneNotSupportedException {
//        UserDto user = new UserDto();
//        user.setUserId(this.getUserId());
//        user.setUserName(this.getUserName());
//        user.setEmail(this.getEmail());
//        user.setName(this.getName());
//        user.setPhoneNo(this.getPhoneNo());
//        return user;
//    }

    public void clone(User user) {
        setUserId(user.getUserId());
        setUserName(user.getUserName());
        setEmail(user.getEmail());
        setName(user.getName());
        setPhoneNo(user.getPhoneNo());
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                '}';
    }

}
