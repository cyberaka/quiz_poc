package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.Topic;
import com.cyberaka.quiz.dto.TopicDto;
import com.cyberaka.quiz.service.TopicService;
import com.cyberaka.quiz.utils.CommonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*")
public class TopicController {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    TopicService topicService;

    @RequestMapping("/topics")
    @ResponseBody
    public List<TopicDto> listTopics() {
        log.info("listTopics()");

        List<TopicDto> results = StreamSupport.stream(topicService.findAll().spliterator(), false)
                .map(this::convertToDto)
                .collect(Collectors.toList());

        if (CommonHelper.getInstance().isAuthenticated()) {
            return results;
        } else {
            return CommonHelper.getInstance().getTwentyPercentOfResults(results);
        }
    }

    private TopicDto convertToDto(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.clone(topic);
        return dto;
    }
}
