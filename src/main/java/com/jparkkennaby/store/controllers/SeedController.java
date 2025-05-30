package com.jparkkennaby.store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.exceptions.DatabaseSeedingException;
import com.jparkkennaby.store.services.SeedService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/seed")
public class SeedController {
    private SeedService seedService;

    @GetMapping()
    public ResponseEntity<Void> seedDatabase() {

        seedService.seedDatabase();

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DatabaseSeedingException.class)
    public ResponseEntity<Void> handhandleDatabseSeedingException() {
        return ResponseEntity.internalServerError().build();
    }

}
