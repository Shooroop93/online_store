package com.example.order_management_system.controller;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@ConfigurationProperties(prefix = "application.notices")
@Data
public class DefaultController {

    private String health;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.status(200).body(health);
    }
}