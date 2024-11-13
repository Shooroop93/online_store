package com.example.order_management_system.service;

import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Transactional
    public Customer save(RegistrationRequest user) {
        Customer customer = new Customer(user.getName(), user.getSurname(), user.getEmail(), user.getPhone(), user.getPhone());
        return customerRepository.save(customer);
    }

    @Transactional
    public void update(int id, Customer updateCustomer) {
        updateCustomer.setId(id);
        customerRepository.save(updateCustomer);
    }

    @Transactional
    public void removeById(int id) {
        customerRepository.deleteById(id);
    }
}