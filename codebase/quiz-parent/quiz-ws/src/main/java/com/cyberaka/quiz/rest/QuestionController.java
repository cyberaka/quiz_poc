package com.cyberaka.quiz.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cyberaka.quiz.service.QuestionService;

@RestController
public class QuestionController {
	
	@Autowired
	QuestionService questionService;
	
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}
}
