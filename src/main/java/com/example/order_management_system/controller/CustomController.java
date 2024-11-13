package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.base_controller.BaseController;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.dto.registration.response.MessageError;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;

import static java.lang.String.format;

@Controller
@Slf4j
@RequiredArgsConstructor
@CustomControllerHandler
public class CustomController extends BaseController {

    private final CustomerService customerService;

    @PostMapping(value = "/reg", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveCustomer(@Validated @RequestBody RegistrationRequest user, BindingResult bindingResult) throws ExceptionValidatedRequestOrResponse {
        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }

        try {
            Customer customer = customerService.save(user);
            RegistrationResponse response = new RegistrationResponse(customer.getId(), format("Пользователь с email %s успешно создан", customer.getEmail()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));
            response.getError().getErrorList().add(format("Пользователь с email %s уже существует", user.getEmail()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}