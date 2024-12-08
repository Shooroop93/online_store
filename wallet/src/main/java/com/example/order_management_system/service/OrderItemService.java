package com.example.order_management_system.service;

import com.example.order_management_system.model.OrderItem;
import com.example.order_management_system.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderItemService{

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public OrderItem findById(int id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    @Transactional
    public void update(int id, OrderItem updateOrderItem) {
        updateOrderItem.setId(id);
        orderItemRepository.save(updateOrderItem);
    }

    @Transactional
    public void removeById(int id) {
        orderItemRepository.deleteById(id);
    }
}