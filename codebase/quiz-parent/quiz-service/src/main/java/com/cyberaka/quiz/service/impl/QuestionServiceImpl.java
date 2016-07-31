package com.cyberaka.quiz.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepository questionRepo;

	@Override
	public Iterable<Question> findQuiz(int topicId, int subTopicId, int level, int count) {
		List<Question> questions = (List<Question>) questionRepo.findQuiz(topicId, subTopicId,level);
		int total = questions.size();
		Random rand = new Random();
		List<Question> results = new ArrayList<>();
		if (count > total) {
			count = total;
		}
		for (int i = 0; i < count; i++) {
			int index = rand.nextInt(total);
			results.add(questions.get(index));
		}

		return results;
	}

}
