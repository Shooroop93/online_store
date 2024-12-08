package com.example.order_management_system.controller;

import com.example.order_management_system.controller.annotation.CustomControllerHandler;
import com.example.order_management_system.controller.exception.ExceptionValidatedRequestOrResponse;
import com.schemes.dto.order.response.OrderResponse;
import com.schemes.dto.shopping_cart.response.ShoppingCartResponse;
import com.example.order_management_system.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.Objects;

import static com.example.order_management_system.constants.OrdersStatus.ERROR;
import static com.example.order_management_system.constants.OrdersStatus.PAID;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Controller
@Slf4j
@RequiredArgsConstructor
@CustomControllerHandler
@RequestMapping(value = "/api/v1/store/order/")
public class OrderController {

    private final OrderService orderService;

    @Value("${application.url.wallet.stub_pay_order}")
    private String urlWalletStubPayOrder;

    private final KafkaTemplate<String, OrderResponse> kafkaTemplate;

    private RestClient getRestClientInstance() {
        return RestClient.create();
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody ShoppingCartResponse createOrderResponse, BindingResult bindingResult, Locale locale)
            throws ExceptionValidatedRequestOrResponse {

        if (bindingResult.hasErrors()) {
            throw new ExceptionValidatedRequestOrResponse(bindingResult);
        }

        OrderResponse response = orderService.createOrder(createOrderResponse, locale);

        if (Objects.isNull(response.getError())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable int id, Locale locale) {

        OrderResponse response = orderService.findById(id, locale);

        if (Objects.isNull(response.getError())) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeOrderBy(@PathVariable int id) {
        try {
            orderService.cancelled(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<?> payOrder(@PathVariable int id, Locale locale) {
        OrderResponse response = orderService.findById(id, locale);

        int statusCode;
        try {
            ResponseEntity<Void> bodilessEntity = getRestClientInstance().post()
                    .uri(urlWalletStubPayOrder)
                    .contentType(APPLICATION_JSON)
                    .body(response)
                    .retrieve().toBodilessEntity();
            statusCode = bodilessEntity.getStatusCode().value();
        } catch (Exception e) {
            statusCode = 400;
        }

        if (statusCode == 200) {
            response.setStatus(PAID.name());
            changeOrderStatus(id, response);
            kafkaTemplate.send("delivery-event-topic", response);
        } else {
            response.setStatus(ERROR.name());
            changeOrderStatus(id, response);
        }

        return ResponseEntity.badRequest().build();
    }

    private void changeOrderStatus(int id, OrderResponse response) {
        orderService.updateOrderStatus(id, response);
    }
}