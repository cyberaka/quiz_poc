package com.cyberaka.quiz.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class PublicController {

    private Logger log = Logger.getLogger(getClass().getName());

    @RequestMapping("/public")
    @ResponseBody
    public String listTopics() {
        log.info("public()");
        return "Public Endpoint.";
    }
}
