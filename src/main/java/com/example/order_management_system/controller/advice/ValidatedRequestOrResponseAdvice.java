package com.example.order_management_system.controller.advice;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.dto.registration.response.MessageError;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.String.format;

@ControllerAdvice(annotations = {CustomControllerHandler.class})
@RequiredArgsConstructor
public class ValidatedRequestOrResponseAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(ExceptionValidatedRequestOrResponse.class)
    public ResponseEntity<RegistrationResponse> handleValidateException(BindException exception, Locale locale) {

        RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));
        String message = messageSource.getMessage("application.controller.validation.message.error", new Object[0], locale);

        exception.getFieldErrors()
                .forEach(fieldError -> response.getError().getErrorList().add(format(message, fieldError.getField(), fieldError.getDefaultMessage())));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}