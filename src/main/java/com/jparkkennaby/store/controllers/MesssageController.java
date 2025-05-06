package com.jparkkennaby.store.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.entities.Message;

@RestController
public class MesssageController {
    
    @RequestMapping("/hello")
    public Message sayHello() {
        return new Message("Hello World!");
    }

}
