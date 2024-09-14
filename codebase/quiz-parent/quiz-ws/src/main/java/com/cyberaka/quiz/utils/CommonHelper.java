package com.cyberaka.quiz.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class CommonHelper {

    private static CommonHelper instance;

    public static synchronized CommonHelper getInstance() {
        if (instance == null) {
            instance = new CommonHelper();
        }
        return instance;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken);
    }

    public <T> List<T> getTwentyPercentOfResults(List<T> results) {
        int size = results.size();
        int twentyPercentCount = Math.max(1, (int) Math.ceil(size * 0.20));
        return results.subList(0, twentyPercentCount);
    }
}
