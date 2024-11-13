package com.example.order_management_system.controller.advice;

import com.example.order_management_system.controller.ExceptionBadRequestForRequisition;
import com.example.order_management_system.dto.registration.response.MessageError;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;

import static java.lang.String.format;

@ControllerAdvice
@RequiredArgsConstructor
public class CustomControllerAdvice {

    @ExceptionHandler(ExceptionBadRequestForRequisition.class)
    public ResponseEntity<RegistrationResponse> handleBindExceptionBadRequest(BindException exception) {
        RegistrationResponse response = new RegistrationResponse(new MessageError(new ArrayList<>()));

        exception.getFieldErrors().forEach(fieldError ->
                response.getError().getErrorList().add(format("Field = %s. Error message: '%s'", fieldError.getField(), fieldError.getDefaultMessage())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}