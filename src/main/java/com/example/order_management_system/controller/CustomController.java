package com.example.order_management_system.controller;

import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.dto.registration.response.MessageError;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import com.example.order_management_system.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Controller
@RequestMapping(value = "/v1/customer/", consumes = "application/json", produces = "application/json")
@Slf4j
public class CustomController {

    private final CustomerService customerService;

    @Autowired
    public CustomController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/reg")
    public ResponseEntity<RegistrationResponse> saveCustomer(@Validated @RequestBody RegistrationRequest user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handlerValidationErrors(bindingResult.getFieldErrors());
        }

        try {
            int customerId = customerService.save(user);
            return buildSuccessResponse(customerId, user.getEmail());
        } catch (Exception e) {
            return buildErrorResponse(user.getEmail());
        }
    }

    private ResponseEntity<RegistrationResponse> handlerValidationErrors(List<FieldError> bindingResult) {
        RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));

        bindingResult.forEach(fieldError ->
                response.getError().getErrorList().add(format("Field = %s. Error message: '%s'", fieldError.getField(), fieldError.getDefaultMessage())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<RegistrationResponse> buildSuccessResponse(int customerId, String email) {
        RegistrationResponse response = new RegistrationResponse();
        response.setId(customerId);
        response.setMessage(format("Пользователь с email %s успешно создан", email));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ResponseEntity<RegistrationResponse> buildErrorResponse(String email) {
        RegistrationResponse response = new RegistrationResponse();
        response.setMessage(format("Пользователь с email %s уже существует", email));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}