package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.PdfGenaratorUtil;
import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.dto.CandidateResultDto;
import com.cyberaka.quiz.dto.QuestionAnswerDto;
import com.cyberaka.quiz.dto.QuestionDto;
import com.cyberaka.quiz.dto.UserDto;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.cyberaka.quiz.service.QuestionService;
import com.cyberaka.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    PdfGenaratorUtil pdfGenaratorUtil;

    @Value("${data.result_output}")
    String dataResultOutputFolder;

    @RequestMapping("/quiz/{topicId}/{subTopicId}/{level}/{count}")
    @ResponseBody
    public List<QuestionDto> getQuiz(@PathVariable("topicId") int topicId, @PathVariable("subTopicId") int subTopicId,
                                     @PathVariable("level") int level, @PathVariable("count") int count) {
        Iterable<Question> questions = questionService.findQuiz(topicId, subTopicId, level, count);
        List<QuestionDto> results = new ArrayList<QuestionDto>();
        for (Question question : questions) {
            QuestionDto dto = new QuestionDto();
            dto.clone(question);
            results.add(dto);
        }
        return results;
    }

    @RequestMapping(value="/quiz/{userId}", method=RequestMethod.POST)
    @ResponseBody
    public void submitQuizAnswer(Model model, @PathVariable("userId") int userId, @RequestBody QuestionAnswerDto[] body) throws QuizSecurityException {
        System.out.println("Received answer submitted by user " + userId);
        System.out.println(body);
        User user = userService.find(userId);
        if (user == null) {
            throw new QuizSecurityException("No valid user id provided!");
        }
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("name",user.getName());

        //Get current date time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy h-mm-a");
        String formatDate = now.format(dateFormatter);
        String formatTime = now.format(timeFormatter);
        String fileFormatterStr = now.format(fileFormatter);
        data.put("date_of_examination", formatDate);
        data.put("time_of_examination", formatTime);

        ArrayList<CandidateResultDto> resultList = new ArrayList<>();
        int sl = 0;
        int score = 0;
        for (QuestionAnswerDto dto: body) {
            CandidateResultDto templateDto = new CandidateResultDto();
            templateDto.setQuestionNumber(++sl);
            templateDto.setQuestion(dto.getQuestion());
            templateDto.setAnswer(dto.getAnswers().stream().map(Object::toString).collect(Collectors.joining(",")));
            templateDto.setCandidateAnswer(dto.getUserAnswers().stream().map(Object::toString).collect(Collectors.joining(",")));

            boolean correct = false;
            if (dto.getAnswers().size() == 1 && (dto.getOptions() == null || dto.getOptions().size() == 0)) {
                // User entry.
                if (dto.getAnswers().get(0).equalsIgnoreCase(dto.getUserAnswers().get(0))) {
                    correct = true;
                }
            } else {
                if (dto.getAnswers().size() == 1) {
                    // Single choice.
                    if (dto.getAnswers().get(0).equalsIgnoreCase(dto.getUserAnswers().get(0))) {
                        correct = true;
                    }
                } else {
                    // Multiple choice.
                    int correctAnswerCount = 0;
                    for (int j = 0; j < dto.getAnswers().size(); j++) {
                        String questionAnswer = dto.getAnswers().get(j);

                        for (int i = 0; i < dto.getUserAnswers().size(); i++) {
                            String userAnswer = dto.getUserAnswers().get(i);
                            if (questionAnswer.equalsIgnoreCase(userAnswer)) {
                                correctAnswerCount++;
                            }
                        }
                    }
                    if (correctAnswerCount == dto.getAnswers().size()) {
                        correct = true;
                    }
                }
            }
            if (correct) {
                templateDto.setCorrect("Yes");
                score++;
            } else {
                templateDto.setCorrect("No");
            }
            resultList.add(templateDto);
        }
        data.put("candidate_score", score + " out of " + body.length);
        data.put("answertable", resultList);

        String fileName = dataResultOutputFolder + File.separator + user.getName() + " " + fileFormatterStr + ".pdf";
        try {
            pdfGenaratorUtil.createPdf("result", fileName, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
