package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.service.CustomerService;
import com.schemes.dto.registration.request.RegistrationRequest;
import com.schemes.dto.registration.response.MessageError;
import com.schemes.dto.registration.response.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        checkErrorsValidatedRequest(user, bindingResult, locale);

        String localizedMessage = null;

        try {
            Customer customer = customerService.save(user);
            localizedMessage = getLocalizedMessage("application.controller.db.customer.message.created", locale);
            RegistrationResponse response = new RegistrationResponse(customer.getId(), format(localizedMessage, customer.getEmail()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));
            localizedMessage = getLocalizedMessage("application.controller.db.customer.message.conflict", locale);
            response.getError().getErrorList().add(format(localizedMessage, user.getEmail()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getCustomer(@PathVariable int id, Locale locale) {
        RegistrationResponse response = customerService.findById(id, locale);
        return Objects.nonNull(response.getError()) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response) :
                ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCustomer(@PathVariable int id) {
        try {
            customerService.removeById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping(value = "/edit/{id}", consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
    public ResponseEntity<?> updateCustomer(@PathVariable int id, @Validated @RequestBody RegistrationRequest request, BindingResult bindingResult, Locale locale)
            throws ExceptionValidatedRequestOrResponse {

        checkErrorsValidatedRequest(request, bindingResult, locale);

        String localizedMessage = null;

        try {
            customerService.update(id, request);
            localizedMessage = getLocalizedMessage("application.controller.db.customer.message.update", locale);
            RegistrationResponse response = new RegistrationResponse(id, format(localizedMessage, id));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));
            localizedMessage = getLocalizedMessage("application.controller.db.customer.message.update.conflict", locale);
            response.getError().getErrorList().add(format(localizedMessage, id));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    private void checkErrorsValidatedRequest(@RequestBody @Validated RegistrationRequest user, BindingResult bindingResult, Locale locale) throws ExceptionValidatedRequestOrResponse {
        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }
    }

    private String getLocalizedMessage(String messageKey, Locale locale, Object... args) {
        return messageSource.getMessage(messageKey, args, locale);
    }
}