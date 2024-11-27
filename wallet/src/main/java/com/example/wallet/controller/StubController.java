package com.example.wallet.controller;

import com.example.wallet.temporary_folder.response.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/wallet/")
public class StubController {

    @PostMapping("/stubPayOrder")
    public ResponseEntity<String> stubPayOrder(@RequestBody OrderResponse orderResponse) {

        boolean pending = "PENDING".equals(orderResponse.getStatus());
        boolean totalAmount = orderResponse.getTotalAmount() != null;

        if (pending && totalAmount) {
            return ResponseEntity.status(HttpStatus.OK).body("Payment processing successful");
        }

        return ResponseEntity.badRequest().body("Invalid order status or missing total amount");
    }
}