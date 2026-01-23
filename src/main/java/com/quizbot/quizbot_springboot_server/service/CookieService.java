package com.quizbot.quizbot_springboot_server.service;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public String buildAuthCookie(String token) {
        return "auth_token=" + token +
                "; Path=/" +
                "; Max-Age=86400" +
                "; HttpOnly" +
                "; SameSite=None" +
                "; Secure";
    }
}

