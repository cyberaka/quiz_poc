package com.cyberaka.quiz.dao;

import org.springframework.data.repository.CrudRepository;

import com.cyberaka.quiz.domain.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
