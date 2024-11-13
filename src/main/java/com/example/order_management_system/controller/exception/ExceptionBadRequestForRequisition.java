package com.example.order_management_system.controller.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

public class ExceptionBadRequestForRequisition extends BindException implements BindingResult {

    public ExceptionBadRequestForRequisition(BindingResult  message) {
        super(message);
    }
}