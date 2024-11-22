package com.example.order_management_system.service;

import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.dto.registration.response.MessageError;
import com.example.order_management_system.dto.registration.response.Participant;
import com.example.order_management_system.dto.registration.response.RegistrationResponse;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RegistrationResponse findById(int id, Locale locale) {
        Optional<Customer> customer = customerRepository.findById(id);
        return createAResponseForARequestToRetrieveACustomer(customer, id, locale);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findById(int id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer save(RegistrationRequest user) {
        Customer customer = new Customer(user.getName(), user.getSurname(), user.getEmail(), user.getPhone(), user.getAddress());
        return customerRepository.save(customer);
    }

    @Transactional
    public void update(int id, RegistrationRequest updateCustomer) {
        Customer customer = new Customer(
                id,
                updateCustomer.getName(),
                updateCustomer.getSurname(),
                updateCustomer.getEmail(),
                updateCustomer.getPhone(),
                updateCustomer.getAddress());

        customerRepository.save(customer);
    }

    @Transactional
    public void removeById(int id) {
        customerRepository.deleteById(id);
    }

    private RegistrationResponse createAResponseForARequestToRetrieveACustomer(Optional<Customer> customer, int id, Locale locale) {
        RegistrationResponse response;

        if (customer.isEmpty()) {
            response = new RegistrationResponse(new MessageError(new ArrayList<>()));
            String message = messageSource.getMessage("application.controller.db.customer.message.get", new Object[0], locale);

            response.setId(id);
            response.getError().getErrorList().add(format(message, id));
        } else {
            response = new RegistrationResponse();
            response.setId(id);
            List<Participant> participants = getParticipants(customer.get());
            response.setParticipants(participants);
        }

        return response;
    }

    private static List<Participant> getParticipants(Customer customer) {

        List<Participant> participants = new ArrayList<>();
        Participant participant = new Participant(
                (long) customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress());
        participants.add(participant);
        return participants;
    }
}