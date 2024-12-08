package com.example.order_management_system.service;

import com.example.order_management_system.model.Payment;
import com.example.order_management_system.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Payment findById(int id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Transactional
    public void update(int id, Payment updatePayment) {
        updatePayment.setId(id);
        paymentRepository.save(updatePayment);
    }

    @Transactional
    public void removeById(int id) {
        paymentRepository.deleteById(id);
    }
}