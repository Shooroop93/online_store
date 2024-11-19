package com.example.order_management_system.service;

import com.example.order_management_system.dto.shopping_cart.request.Item;
import com.example.order_management_system.dto.shopping_cart.request.ShoppingCartRequest;
import com.example.order_management_system.dto.shopping_cart.response.ShoppingCartResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.model.Product;
import com.example.order_management_system.model.ShoppingCart;
import com.example.order_management_system.repository.CustomerRepository;
import com.example.order_management_system.repository.ProductRepository;
import com.example.order_management_system.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;

    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public void findAll() {

    }

    @Transactional(readOnly = true)
    public void findById(int id, Locale locale) {

    }

    @Transactional
    public ShoppingCartResponse save(ShoppingCartRequest shoppingCartRequest, Locale locale) {
        ShoppingCartResponse response = new ShoppingCartResponse();

        for (Item item : shoppingCartRequest.getItemsList()) {
            Optional<Customer> customer = customerRepository.findById(item.getOwnerId());

            if (customer.isEmpty()) {
                String message = messageSource.getMessage("application.controller.db.customer.message.get", new Object[0], locale);
                response.addError(format(message, item.getOwnerId()));
                continue;
            }

            Customer customerOptional = customer.get();
            Optional<Product> firstProduct = customerOptional.getProductsList().stream().filter(product -> product.getId() == item.getArticle()).findFirst();

            if (firstProduct.isEmpty()) {
                String message = messageSource.getMessage("application.controller.db.product.message.get", new Object[0], locale);
                response.addError(format(message, item.getOwnerId(), item.getArticle()));
                continue;
            }

            Optional<ShoppingCart> shoppingCartOptional = customerOptional.getShoppingCarts().stream()
                    .filter(shoppingCart -> shoppingCart.getProduct().getId() == item.getArticle())
                    .findFirst();

            if (shoppingCartOptional.isPresent()) {
                String message = messageSource.getMessage("application.controller.db.shopping_cart.message.conflict", new Object[0], locale);
                response.addError(format(message, item.getArticle(), item.getOwnerId()));
                continue;
            }

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setCustomer(customerOptional);
            shoppingCart.setProduct(firstProduct.get());
            shoppingCart.setCount(item.getQuantity());

            shoppingCartRepository.save(shoppingCart);

            if (Objects.isNull(response.getMessage())) {
                String message = messageSource.getMessage("application.controller.db.shopping_cart.message.created", new Object[0], locale);
                response.setMessage(message);
            }
        }

        return response;
    }

    @Transactional
    public void update(int id, Product updateProduct) {

    }

    @Transactional
    public void removeById(int id) {
        shoppingCartRepository.deleteById(id);
    }
}