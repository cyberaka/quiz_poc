package com.cyberaka.quiz.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {
	@RequestMapping("/")
	@ResponseBody
	String home() {
		return "Hello World!";
	}
}
