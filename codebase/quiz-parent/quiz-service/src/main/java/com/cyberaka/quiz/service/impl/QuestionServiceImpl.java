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
		List<Question> questions = (List<Question>) questionRepo.findQuiz(topicId, subTopicId, level);
		int total = questions.size();
		Random rand = new Random();
		List<Question> results = new ArrayList<>();
		if (count > total) {
			count = total;
		}
		List<Integer> options = new ArrayList<Integer>();
		for (int i = 0; i < count; i++) {

			int index = rand.nextInt(total);
			while (options.contains(index)) {
				System.err.println("Duplicate Index Ignored >> " + index);
				index = rand.nextInt(total);
			}
			results.add(questions.get(index));
			options.add(index);
			System.out.println("Index Added >> " + index);
		}
		System.out.println("Total Questions Selected >> " + results.size());
		return results;
	}

}
