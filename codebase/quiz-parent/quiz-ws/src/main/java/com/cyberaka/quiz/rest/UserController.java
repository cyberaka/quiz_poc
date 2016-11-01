package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.dto.UserDto;
import com.cyberaka.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/login/{userName}/{password}")
    @ResponseBody
    public UserDto login(@PathVariable("userName") String userName, @PathVariable("password") String password) {
        User user = userService.login(userName, password);
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.clone(user);
        return userDto;
    }

}
