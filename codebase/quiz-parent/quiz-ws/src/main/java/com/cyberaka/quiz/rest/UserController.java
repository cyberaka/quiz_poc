package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.dto.UserDto;
import com.cyberaka.quiz.dto.common.exception.QuizException;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.cyberaka.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/login", method= RequestMethod.POST)
    @ResponseBody
    public UserDto login(@RequestHeader("userName") String userName, @RequestHeader("password") String password) throws QuizSecurityException {
        User user = userService.login(userName, password);
        if (user == null) {
            throw new QuizSecurityException("Invalid User");
        }
        UserDto userDto = new UserDto();
        userDto.clone(user);
        return userDto;
    }

}
