package com.example.order_management_system.service;

import com.example.order_management_system.dto.order.response.OrderResponse;
import com.example.order_management_system.dto.shopping_cart.response.ItemShoppingCart;
import com.example.order_management_system.dto.shopping_cart.response.ShoppingCartResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.model.Order;
import com.example.order_management_system.model.OrderItem;
import com.example.order_management_system.model.Product;
import com.example.order_management_system.repository.CustomerRepository;
import com.example.order_management_system.repository.OrderItemRepository;
import com.example.order_management_system.repository.OrderRepository;
import com.example.order_management_system.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;
    private final OrderItemService orderItemService;

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    public OrderResponse save(ShoppingCartResponse requestCreateOrder, Locale locale) {
        OrderResponse orderResponse = new OrderResponse(requestCreateOrder.getIdOwnerShoppingCart());
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (requestCreateOrder.getItemList().isEmpty()) {
            String message = messageSource.getMessage("application.controller.db.shopping_cart.message.get", new Object[0], locale);
            orderResponse.addError(format(message, requestCreateOrder.getIdOwnerShoppingCart()));
            return orderResponse;
        }

        Optional<Customer> customer = customerRepository.findById(requestCreateOrder.getIdOwnerShoppingCart());

        if (customer.isEmpty()) {
            String message = messageSource.getMessage("application.controller.db.customer.message.get", new Object[0], locale);
            orderResponse.addError(format(message, requestCreateOrder.getIdOwnerShoppingCart()));
            return orderResponse;
        }

        Order order = new Order();
        order.setOwner(new Customer(requestCreateOrder.getIdOwnerShoppingCart()));
        order.setStatus("Pending");
        order.setTotalAmount(BigDecimal.ZERO);
        Order save = orderRepository.save(order);

        for (ItemShoppingCart itemShoppingCart : requestCreateOrder.getItemList()) {
            Product productDB = productRepository.findById(itemShoppingCart.getArticle()).get();

            if (productDB.getQuantity() < itemShoppingCart.getQuantity()) {
                String message = messageSource.getMessage("application.controller.db.order.message.not_found", new Object[0], locale);
                orderResponse.addError(format(message, itemShoppingCart.getArticle(), itemShoppingCart.getOwnerId()));
                continue;
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrders(save);
            orderItem.setProduct(productDB);
            orderItem.setQuantity(itemShoppingCart.getQuantity());
            orderItem.setPrice(itemShoppingCart.getPrice());
            orderItemRepository.save(orderItem);

            productDB.setQuantity(productDB.getQuantity() - itemShoppingCart.getQuantity());
            productRepository.save(productDB);

            totalAmount = totalAmount.add(itemShoppingCart.getPrice().multiply(BigDecimal.valueOf(itemShoppingCart.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        orderResponse.setTotalAmount(totalAmount);
        orderResponse.setOrderNumber(order.getId());
        orderResponse.setStatus(order.getStatus());

        return orderResponse;
    }

    @Transactional
    public void update(int id, Order updateOrder) {
        updateOrder.setId(id);
        orderRepository.save(updateOrder);
    }

    @Transactional
    public void removeById(int id) {
        orderRepository.deleteById(id);
    }
}