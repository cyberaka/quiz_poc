package com.cyberaka.quiz.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.dto.SubTopicDto;
import com.cyberaka.quiz.service.SubTopicService;

@RestController
public class SubTopicController {
    @Autowired
    SubTopicService subTopicService;

    @RequestMapping("/subtopics/{topicID}")
    @ResponseBody
    public List<SubTopicDto> findByTopic(@PathVariable("topicID") int topicId) {
        Iterable<SubTopic> subTopics = subTopicService.findByTopic(topicId);
        List<SubTopicDto> results = new ArrayList<SubTopicDto>();
        for (SubTopic subTopic : subTopics) {
            SubTopicDto dto = new SubTopicDto();
            dto.clone(subTopic);
            results.add(dto);
        }
        return results;
    }
}
