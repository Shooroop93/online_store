package com.example.order_management_system.service;

import com.example.order_management_system.constants.OrdersStatus;
import com.example.order_management_system.dto.order.response.OrderItemResponse;
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
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.example.order_management_system.constants.OrdersStatus.CANCELLED;
import static com.example.order_management_system.constants.OrdersStatus.PENDING;
import static java.lang.String.format;
import static org.apache.logging.log4j.Level.ERROR;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(int id, Locale locale) {
        Optional<Order> byId = orderRepository.findById(id);
        OrderResponse orderResponse = new OrderResponse();

        if (byId.isEmpty()) {
            String message = getLocalizedMessage("application.controller.db.order.message.conflict", locale);
            orderResponse.addError(format(message, id));
            return orderResponse;
        }

        for (OrderItem orderItem : byId.get().getOrderItemList()) {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setArticle(orderItem.getProduct().getId());
            orderItemResponse.setItemName(orderItem.getProduct().getItemName());
            orderItemResponse.setDescription(orderItem.getProduct().getDescription());
            orderItemResponse.setPrice(orderItem.getPrice());
            orderItemResponse.setQuantity(orderItem.getQuantity());

            orderResponse.addItem(orderItemResponse);
        }

        orderResponse.setIdOwnerShoppingCart(byId.get().getOwner().getId());
        orderResponse.setOrderNumber(id);
        orderResponse.setStatus(byId.get().getStatus());

        return orderResponse;
    }

    @Transactional
    public OrderResponse createOrder(ShoppingCartResponse requestCreateOrder, Locale locale) {
        OrderResponse orderResponse = new OrderResponse(requestCreateOrder.getIdOwnerShoppingCart());

        if (requestCreateOrder.getItemList().isEmpty()) {
            String message = getLocalizedMessage("application.controller.db.shopping_cart.message.get", locale);
            orderResponse.addError(format(message, requestCreateOrder.getIdOwnerShoppingCart()));
            orderResponse.setStatus(OrdersStatus.ERROR.name());
            return orderResponse;
        }

        Optional<Customer> customer = customerRepository.findById(requestCreateOrder.getIdOwnerShoppingCart());

        if (customer.isEmpty()) {
            String message = getLocalizedMessage("application.controller.db.customer.message.get", locale);
            orderResponse.addError(format(message, requestCreateOrder.getIdOwnerShoppingCart()));
            orderResponse.setStatus(OrdersStatus.ERROR.name());
            return orderResponse;
        }

        Order save = orderRepository.save(new Order(customer.get(), PENDING.name(), BigDecimal.ZERO));

        List<OrderItem> orderItemsList = new ArrayList<>();
        List<Product> productList = new ArrayList<>();

        BigDecimal totalAmount = prepareDataForItemListAndProduct(save, requestCreateOrder, locale, orderResponse, orderItemsList, productList);

        if (Objects.nonNull(orderResponse.getError())) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            completeOrderResponse(orderResponse, BigDecimal.ZERO, -1, ERROR.name());
        } else {
            save.setTotalAmount(totalAmount);
            writeDataToTheDatabase(orderItemsList, productList, save);
            completeOrderResponse(orderResponse, totalAmount, save.getId(), save.getStatus());
        }

        return orderResponse;
    }

    @Transactional
    public void update(int id, Order updateOrder) {
        updateOrder.setId(id);
        orderRepository.save(updateOrder);
    }

    @Transactional
    public void cancelled(int id) {
        Optional<Order> orderDB = orderRepository.findById(id);
        if (orderDB.isEmpty()) {
            throw new RuntimeException("Заказ отсутствует");
        }

        Order order = orderDB.get();

        if (order.getStatus().equals(ERROR.name()) || order.getStatus().equals(CANCELLED.name())) {
            throw new RuntimeException("Не активный заказ");
        }

        for (OrderItem orderItem : order.getOrderItemList()) {
            Product product = productRepository.findById(orderItem.getProduct().getId()).get();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrdersStatus.CANCELLED.name());
    }

    private String getLocalizedMessage(String messageKey, Locale locale, Object... args) {
        return messageSource.getMessage(messageKey, args, locale);
    }

    private OrderItem createOrderItem(Order save, Product productDB, int quantity, BigDecimal price) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrders(save);
        orderItem.setProduct(productDB);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(price);
        return orderItem;
    }

    private void completeOrderResponse(OrderResponse orderResponse, BigDecimal totalAmount, int id, String status) {
        orderResponse.setTotalAmount(totalAmount);
        orderResponse.setOrderNumber(id);
        orderResponse.setStatus(status);
    }

    private BigDecimal prepareDataForItemListAndProduct(Order save, ShoppingCartResponse requestCreateOrder, Locale locale, OrderResponse orderResponse, List<OrderItem> orderItemsList, List<Product> productList) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (ItemShoppingCart itemShoppingCart : requestCreateOrder.getItemList()) {
            Optional<Product> productRepositoryById = productRepository.findById(itemShoppingCart.getArticle());
            if (productRepositoryById.isPresent()) {
                Product productDB = productRepositoryById.get();

                if (productDB.getQuantity() < itemShoppingCart.getQuantity()) {
                    String message = getLocalizedMessage("application.controller.db.order.message.not_found", locale);
                    orderResponse.addError(format(message, itemShoppingCart.getArticle(), itemShoppingCart.getOwnerId()));
                    continue;
                }

                productDB.setQuantity(productDB.getQuantity() - itemShoppingCart.getQuantity());

                orderItemsList.add(createOrderItem(save, productDB, itemShoppingCart.getQuantity(), itemShoppingCart.getPrice()));
                productList.add(productDB);
                totalAmount = totalAmount.add(itemShoppingCart.getPrice().multiply(BigDecimal.valueOf(itemShoppingCart.getQuantity())));
            } else {
                String message = getLocalizedMessage("application.controller.db.product.message.get", locale);
                orderResponse.addError(format(message, itemShoppingCart.getOwnerId()));
            }
        }
        return totalAmount;
    }

    private void writeDataToTheDatabase(List<OrderItem> orderItemsList, List<Product> productList, Order save) {
        orderItemRepository.saveAll(orderItemsList);
        productRepository.saveAll(productList);
        orderRepository.save(save);
    }
}