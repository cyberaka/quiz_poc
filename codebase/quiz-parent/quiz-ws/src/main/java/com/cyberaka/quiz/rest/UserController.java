package com.cyberaka.quiz.rest;

import com.cyberaka.quiz.domain.User;
import com.cyberaka.quiz.dto.UserDto;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.cyberaka.quiz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    private Logger log = Logger.getLogger(getClass().getName());

    @Autowired
    UserService userService;

    @RequestMapping(value="/login", method= RequestMethod.GET)
    @ResponseBody
    public UserDto login(@RequestParam("userName") String userName, @RequestParam("userPassword") String userPassword) throws QuizSecurityException {
        log.info("login(" + userName + ", " + userPassword + ")");
        User user = userService.login(userName, userPassword);
        if (user == null) {
            throw new QuizSecurityException("Invalid User");
        }
        UserDto userDto = new UserDto();
        userDto.clone(user);
        return userDto;
    }

    @RequestMapping(value="/delete_user", method= RequestMethod.DELETE)
    @ResponseBody
    public boolean deleteUser() throws QuizSecurityException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();


        log.info("deleteUser()");
        return true;
    }

}
