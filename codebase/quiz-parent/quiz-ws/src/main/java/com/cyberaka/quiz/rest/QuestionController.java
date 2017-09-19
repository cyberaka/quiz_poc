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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = now.format(formatter);
        data.put("date_of_examination", formatDateTime);
        data.put("candidate_score", "1");

        ArrayList<CandidateResultDto> resultList = new ArrayList<>();
        int sl = 0;
        for (QuestionAnswerDto dto: body) {
            CandidateResultDto templateDto = new CandidateResultDto();
            templateDto.setQuestionNumber(++sl);
            templateDto.setQuestion(dto.getQuestion());
            templateDto.setCandidateAnswer(dto.getUserAnswers().toString());
            templateDto.setCorrect("Yes");
            resultList.add(templateDto);
        }
        data.put("answertable", resultList);
        model.addAttribute("answertable", resultList);

        String fileName = dataResultOutputFolder + File.separator + user.getName() + ".pdf";
        try {
            pdfGenaratorUtil.createPdf("result", fileName, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
