package com.example.order_management_system.service;

import com.example.order_management_system.model.Order;
import com.example.order_management_system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(int id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Order order) {
        orderRepository.save(order);
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