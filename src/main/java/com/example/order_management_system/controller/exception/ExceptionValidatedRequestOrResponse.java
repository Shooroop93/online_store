package com.example.order_management_system.controller.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class ExceptionValidatedRequestOrResponse extends BindException implements BindingResult {

    public ExceptionValidatedRequestOrResponse(BindingResult  message) {
        super(message);
    }
}