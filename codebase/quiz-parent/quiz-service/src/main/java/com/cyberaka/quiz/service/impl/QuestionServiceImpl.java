package com.cyberaka.quiz.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cyberaka.quiz.dao.QuestionRepository;
import com.cyberaka.quiz.dao.SubTopicRepository;
import com.cyberaka.quiz.dao.TopicRepository;
import com.cyberaka.quiz.dao.UserRepository;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.service.QuestionService;

@Service
public class QuestionServiceImpl implements QuestionService {

	@Autowired
	QuestionRepository questionRepo;
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	TopicRepository topicRepo;
	
	@Autowired
	SubTopicRepository subtopicRepo;
	
	@Override
	public void bootup() {
		User user = new User();
		user.setAdmin(true);
		user.setConsumer(true);
		user.setPublisher(true);
		user.setEmail("cyberaka@gmail.com");
		user.setPassword("1234");
		user.setPhoneNo("1234");
		user.setUserName("cyberaka");
		user.setName("Abhinav Anand");
		userRepo.save(user);
		
		Topic topic = new Topic();
		topic.setTitle("Java");
		topicRepo.save(topic);
		
		SubTopic subTopic = new SubTopic();
		subTopic.setTitle("Section 1");
		subTopic.setTopic(topic);
		subtopicRepo.save(subTopic);
		
		TextToSqlConverter converter = new TextToSqlConverter("C:\\Abhinav\\Quiz\\quiz_poc\\documents\\questions\\Section1.txt");
		try {
			converter.bootup(topic, subTopic, user);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class TextToSqlConverter {

		private String fileName;

		public TextToSqlConverter(String fileName) {
			this.fileName = fileName;
		}

		public void bootup(Topic topic, SubTopic subTopic, User user) throws IOException {
			ArrayList<String> lines = readFile();
			ArrayList<String[]> list = parseQuestions(lines.toArray(new String[0]));
			
			for (int i=0; i<list.size(); i++) {
				String[] line = list.get(i);
				System.out.println(String.format("Question:\n%s\n Options:\n%s\n Answers:\n%s\n=========\n",
						line[1], line[3], line[4]));
			
				Question quest = new Question();
				quest.setTopic(topic);
				quest.setSubTopic(subTopic);
				quest.setContributer(user);
				quest.setQuestion(line[1]);
				quest.setOptions(line[3]);
				quest.setAnswers(line[4]);
				questionRepo.save(quest);
			}
//			dumpInsertStatements(list);
		}

		private void dumpInsertStatements(ArrayList<String[]> list) {
			for (int i=0; i<list.size(); i++) {
				String[] line = list.get(i);
				System.out.println(String.format("Question:\n%s\n Options:\n%s\n Answers:\n%s\n=========\n",
						line[1], line[3], line[4]));
			}
		}

		private ArrayList<String[]> parseQuestions(String[] lines) {
			ArrayList<String[]> questions = new ArrayList<>();
			StringBuilder questionLines = new StringBuilder();
			for (int i=0; i<lines.length; i++) {
				if (lines[i].startsWith("Q. ")) {
					if (questionLines.length() != 0) {
						questions.add(parseQuestion(0, questionLines.toString()));
						questionLines.setLength(0);
					}
				}
				questionLines.append(lines[i] + "\n");
			}
			if (questionLines.length() != 0) {
				questions.add(parseQuestion(0, questionLines.toString()));
			}
			return questions;
		}
		
		// This function reads the complete mixed up question/exhibit/answer
		// text and assigns them into the currentExamQuestionList array
		private String[] parseQuestion(int sectionNo, String mixedUp) {
			String[] parsedQuestion = new String[8];
			boolean isExhibit = false;
			int exhibitPos = 0;
			boolean isOptions = false;
			int optionsPos = 0;
			boolean isAnswer = false;
			int answerPos = 0;
			String tempQuestion = "";
			String tempExhibit = "";
			String tempOptions = "";
			String tempAnswer = "";
			parsedQuestion[0] = "";
			parsedQuestion[1] = "";
			parsedQuestion[2] = "";
			parsedQuestion[3] = "";
			parsedQuestion[4] = "";
			parsedQuestion[5] = "";
			parsedQuestion[6] = "";
			parsedQuestion[7] = "";

			exhibitPos = mixedUp.indexOf("<EXHIBIT>");
			if (exhibitPos != -1)
				isExhibit = true;

			optionsPos = mixedUp.indexOf("A. ");
			if (optionsPos != -1)
				isOptions = true;

			answerPos = mixedUp.indexOf("ANSWER:");
			if (answerPos != -1)
				isAnswer = true;

			// Set the section no.
			parsedQuestion[0] = (new Integer(sectionNo)).toString();

			// Now we will extract the question
			if (isExhibit) {
				tempQuestion = mixedUp.substring(0, exhibitPos - 1);
			} else if (isOptions) {
				tempQuestion = mixedUp.substring(0, optionsPos - 1);
			} else if (isAnswer) {
				tempQuestion = mixedUp.substring(0, answerPos - 1);
			}
			// Set the question
			parsedQuestion[1] = tempQuestion.substring(3,
					tempQuestion.length()).trim();

			// Now we will extract the exhibit
			if (isExhibit) {
				if (isOptions) {
					tempExhibit = mixedUp.substring(exhibitPos, optionsPos - 1);
				} else if (isAnswer) {
					tempExhibit = mixedUp.substring(exhibitPos, answerPos - 1);
				}
			}
			// Set the exhibit
			parsedQuestion[2] = tempExhibit.trim();

			// Now we will extract the options
			if (isOptions) {
				if (isAnswer) {
					tempOptions = mixedUp.substring(optionsPos, answerPos - 1);
				}
			}
			// Set the options
			parsedQuestion[3] = tempOptions.trim();

			// Now we will extract the answer
			if (isAnswer) {
				tempAnswer = mixedUp.substring(answerPos, mixedUp.length());
			}
			// Set the options
			parsedQuestion[4] = tempAnswer.substring(7,
					tempAnswer.length()).trim();

			parsedQuestion[5] = "";
			parsedQuestion[6] = "";

			// parsedQuestion
			// section no.
			// Question
			// Exhibit
			// Options
			// Answer key
			// Status - Answer given list (a,b,c,d / a / 0x6)
			// Marked - Marked
			return parsedQuestion;
		}

		private ArrayList<String> readFile() throws IOException {
			ArrayList<String> lines = new ArrayList<String>();
			try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
				stream.forEach(lines::add);
			}
			return lines;
		}

	}

}
