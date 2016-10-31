package com.cyberaka.quiz.dao;

import com.cyberaka.quiz.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
