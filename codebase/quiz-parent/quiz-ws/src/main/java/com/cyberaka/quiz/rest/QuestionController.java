package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.Question;
import com.cyberaka.quiz.dto.QuestionDto;
import com.cyberaka.quiz.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class QuestionController {

    @Autowired
    QuestionService questionService;

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

}
