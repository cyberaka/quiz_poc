package com.cyberaka.quiz.exception;

import com.cyberaka.quiz.dto.common.exception.QuizException;
import com.cyberaka.quiz.dto.common.exception.QuizExceptionDto;
import com.cyberaka.quiz.dto.common.exception.QuizSecurityException;
import com.cyberaka.quiz.rest.UserController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Abhinav on 9/17/17.
 */
@ControllerAdvice(basePackageClasses = UserController.class)
public class QuizGlobalExceptionAdvise extends ResponseEntityExceptionHandler {

    @ExceptionHandler(QuizException.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, QuizException ex) {
        HttpStatus status = getStatus(request);
        return new ResponseEntity<>(new QuizExceptionDto(status.value(), ex.getMessage()), status);
    }

    @ExceptionHandler(QuizSecurityException.class)
    @ResponseBody
    ResponseEntity<?> handleControllerException(HttpServletRequest request, QuizSecurityException ex) {
        return new ResponseEntity<>(new QuizExceptionDto(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
