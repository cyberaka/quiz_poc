package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.dto.UserDto;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.cyberaka.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/login", method= RequestMethod.GET)
    @ResponseBody
    public UserDto login(@RequestParam("userName") String userName, @RequestParam("userPassword") String userPassword) throws QuizSecurityException {
        User user = userService.login(userName, userPassword);
        if (user == null) {
            throw new QuizSecurityException("Invalid User");
        }
        UserDto userDto = new UserDto();
        userDto.clone(user);
        return userDto;
    }

}
