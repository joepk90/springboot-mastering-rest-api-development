package com.jparkkennaby.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MesssageController {
    
    @RequestMapping("/hello")
    public String sayHello() {
        return "Hello World!";
    }
    
}
