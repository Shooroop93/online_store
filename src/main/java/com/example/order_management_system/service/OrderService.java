package com.example.order_management_system.service;

import com.example.order_management_system.model.Order;
import com.example.order_management_system.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private OrderRepository orderRepository;

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
    public void save(Order customer) {
        orderRepository.save(customer);
    }

    @Transactional
    public void update(int id, Order updateCustomer) {
        updateCustomer.setId(id);
        orderRepository.save(updateCustomer);
    }

    @Transactional
    public void removeById(int id) {
        orderRepository.deleteById(id);
    }
}