package com.legal.casemanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Serve the index page as the default/home page
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    /**
     * Serve the login page
     */
    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }
}
