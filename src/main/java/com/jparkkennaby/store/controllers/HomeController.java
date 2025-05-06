package com.jparkkennaby.store.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

    // model is a container for data. we use it to pass data from a controller to a view

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "Joe");
        return "index";
    }
}
