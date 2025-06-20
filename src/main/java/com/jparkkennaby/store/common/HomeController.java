package com.jparkkennaby.store.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    // model is a container for data. we use it to pass data from a controller to a
    // view

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("name", "World");
        return "index";
    }
}
