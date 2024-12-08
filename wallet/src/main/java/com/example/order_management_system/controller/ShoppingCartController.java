package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.dto.shopping_cart.request.ShoppingCartRequest;
import com.example.order_management_system.dto.shopping_cart.response.ShoppingCartResponse;
import com.example.order_management_system.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Objects;

@Controller
@Slf4j
@RequiredArgsConstructor
@CustomControllerHandler
@RequestMapping(value = "/api/v1/store/items/shopping_cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PostMapping("/addItem")
    public ResponseEntity<?> addItem(@Validated @RequestBody ShoppingCartRequest shoppingCartRequest, BindingResult bindingResult, Locale locale)
            throws ExceptionValidatedRequestOrResponse {

        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }

        ShoppingCartResponse shoppingCartResponse = shoppingCartService.save(shoppingCartRequest, locale);

        if (Objects.isNull(shoppingCartResponse.getError())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(shoppingCartResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(shoppingCartResponse);
        }
    }

    @GetMapping("/getAllItem/{id}")
    public ResponseEntity<?> getAllItem(@PathVariable int id, Locale locale) {
        ShoppingCartResponse response = shoppingCartService.findById(id, locale);

        if (Objects.isNull(response.getError())) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeItemForShoppingCartByID(@PathVariable int id) {
        try {
            shoppingCartService.removeById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}