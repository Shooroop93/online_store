package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.dto.order.response.OrderResponse;
import com.example.order_management_system.dto.shopping_cart.response.ShoppingCartResponse;
import com.example.order_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Objects;

@Controller
@Slf4j
@RequiredArgsConstructor
@CustomControllerHandler
@RequestMapping(value = "/api/v1/store/order/")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody ShoppingCartResponse createOrderResponse, BindingResult bindingResult, Locale locale)
            throws ExceptionValidatedRequestOrResponse {

        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }

        OrderResponse response = orderService.save(createOrderResponse, locale);

        if (Objects.isNull(response.getError())) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}