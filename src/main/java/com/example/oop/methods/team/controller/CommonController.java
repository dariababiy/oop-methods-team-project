package com.example.oop.methods.team.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CommonController {

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String error() {
        return "error";
    }

}
