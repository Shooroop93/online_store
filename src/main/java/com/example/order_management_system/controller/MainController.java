package com.example.order_management_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Up");
    }
}