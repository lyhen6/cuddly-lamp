package com.ly.cuddlylamp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginController {

    @GetMapping("/byPwd")
    public String login(String login, String pwd){
        return "login ----- > " + login + ", " + " pwd ----> " + pwd;
    }
}
