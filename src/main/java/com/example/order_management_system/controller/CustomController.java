package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.dto.registration.response.MessageError;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static java.lang.String.format;

@Controller
@Slf4j
@RequiredArgsConstructor
@CustomControllerHandler
@RequestMapping(value = "/api/v1/store/client/")
public class CustomController {

    private final CustomerService customerService;
    private final MessageSource messageSource;

    @PostMapping(value = "/reg", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> saveCustomer(@Validated @RequestBody RegistrationRequest user, BindingResult bindingResult, Locale locale) throws ExceptionValidatedRequestOrResponse {
        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }

        try {
            Customer customer = customerService.save(user);
            String messageCreated = messageSource.getMessage("application.controller.db.customer.message.created", new Object[0], locale);
            RegistrationResponse response = new RegistrationResponse(customer.getId(), format(messageCreated, customer.getEmail()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));
            String messageConflict = messageSource.getMessage("application.controller.db.customer.message.conflict", new Object[0], locale);
            response.getError().getErrorList().add(format(messageConflict, user.getEmail()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> getCustomer(@PathVariable int id, Locale locale) {
        RegistrationResponse response = customerService.findById(id, locale);
        return Objects.nonNull(response.getError()) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) :
                ResponseEntity.ok().body(response);
    }
}