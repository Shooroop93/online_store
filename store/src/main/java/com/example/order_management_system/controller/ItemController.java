package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.example.order_management_system.service.ProductService;
import com.schemes.dto.items.request.ItemRequest;
import com.schemes.dto.items.response.ItemsResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Locale;
import java.util.Objects;

import static java.lang.String.format;

@Controller
@Slf4j
@RequiredArgsConstructor
@CustomControllerHandler
@RequestMapping(value = "/api/v1/store/items/")
public class ItemController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @PostMapping("/addItem")
    public ResponseEntity<?> addItem(@Validated @RequestBody ItemRequest itemRequest, BindingResult bindingResult, Locale locale)
            throws ExceptionValidatedRequestOrResponse {

        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }

        try {
            productService.save(itemRequest);
            String message = messageSource.getMessage("application.controller.db.product.message.created", new Object[0], locale);
            ItemsResponse response = new ItemsResponse(itemRequest.getOwnerId(), format(message, itemRequest.getOwnerId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ItemsResponse response = new ItemsResponse();
            String message = messageSource.getMessage("application.controller.db.product.message.conflict", new Object[0], locale);
            response.addError(format(message, itemRequest.getOwnerId()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAllProductForCustomer(@PathVariable int id, Locale locale) {

        ItemsResponse itemsResponse = productService.findById(id,locale);
        return Objects.nonNull(itemsResponse.getError()) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(itemsResponse) :
                ResponseEntity.ok().body(itemsResponse);
    }

    @GetMapping("/getAllProduct")
    public ResponseEntity<?> getAllProductAllCustomer(Locale locale) {

        ItemsResponse itemsResponse = productService.findAll(locale);
        return Objects.nonNull(itemsResponse.getError()) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(itemsResponse) :
                ResponseEntity.ok().body(itemsResponse);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeItemByID(@PathVariable int id) {
        try {
            productService.removeById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}