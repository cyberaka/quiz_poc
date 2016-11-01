package com.cyberaka.quiz.service;

import com.cyberaka.quiz.domain.User;

public interface UserService {

    public User login(String userName, String password);

}
