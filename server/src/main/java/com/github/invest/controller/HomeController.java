package com.github.invest.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String index(HttpServletRequest req) {
        log.info("Session ID: {}", req.getSession().getId()); // Creates the user session.
        return "index.html";
    }
}
