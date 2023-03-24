package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.dto.SubTopicDto;
import com.cyberaka.quiz.service.SubTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class SubTopicController {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    SubTopicService subTopicService;

    @RequestMapping("/subtopics/{topicID}")
    @ResponseBody
    public List<SubTopicDto> findByTopic(@PathVariable("topicID") int topicId) {
        log.info("findByTopic(" + topicId + ")");
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
