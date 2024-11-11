package com.example.order_management_system.controller;

import com.example.order_management_system.controller.base_controller.BaseController;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@ConfigurationProperties(prefix = "application.notices")
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class DefaultController extends BaseController {

    private String health;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.status(HttpStatus.OK).body(health);
    }
}