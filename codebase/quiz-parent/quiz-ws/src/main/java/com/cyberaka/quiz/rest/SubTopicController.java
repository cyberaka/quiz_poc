package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.SubTopic;
import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.dto.SubTopicDto;
import com.cyberaka.quiz.dto.TopicDto;
import com.cyberaka.quiz.service.SubTopicService;
import com.cyberaka.quiz.utils.CommonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*")
public class SubTopicController {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    SubTopicService subTopicService;

    @RequestMapping("/subtopics/{topicID}")
    @ResponseBody
    public List<SubTopicDto> findByTopic(@PathVariable("topicID") String topicId) {
        log.info("findByTopic(" + topicId + ")");

        List<SubTopicDto> results = StreamSupport.stream(subTopicService.findByTopic(topicId).spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());

        if (CommonHelper.getInstance().isAuthenticated()) {
            return results;
        } else {
            return CommonHelper.getInstance().getTwentyPercentOfResults(results);
        }
    }

    private SubTopicDto convertToDto(SubTopic subTopic) {
        SubTopicDto dto = new SubTopicDto();
        dto.clone(subTopic);
        return dto;
    }
}
