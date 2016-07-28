package com.cyberaka.quiz.dao;

import org.springframework.data.repository.CrudRepository;

import com.cyberaka.quiz.domain.Question;

public interface QuestionRepository extends CrudRepository<Question, Integer> {

}
