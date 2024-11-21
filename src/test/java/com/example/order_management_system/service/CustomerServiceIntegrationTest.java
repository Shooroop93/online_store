package com.example.order_management_system.service;

import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class CustomerServiceIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void clearDB() {
        customerRepository.deleteAll();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    void testFindById() {
        Customer customer = new Customer("Test", "Testov", "test@test.com", "+100000000321", "test 123 456");
        int id = customerRepository.save(customer).getId();

        Optional<Customer> foundCustomer = customerService.findById(id);

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer, foundCustomer.get());
    }

    @Test
    void test_update() {
        Customer customer = new Customer("Test123", "Testov123", "test@test3123.com", "+100405000321", "test 645 456");
        RegistrationRequest request = new RegistrationRequest("Test", "Testov", "test@test.com", "test 123 456", "+100000000321");
        int id = customerRepository.save(customer).getId();
        customerService.update(id, request);

        Optional<Customer> customerRepositoryById = customerRepository.findById(id);

        assertTrue(customerRepositoryById.isPresent());
        assertEquals(customerRepositoryById.get().getFirstName(), request.getName());
        assertEquals(customerRepositoryById.get().getLastName(), request.getSurname());
        assertEquals(customerRepositoryById.get().getEmail(), request.getEmail());
        assertEquals(customerRepositoryById.get().getAddress(), request.getAddress());
        assertEquals(customerRepositoryById.get().getPhone(), request.getPhone());
    }

    @Test
    void test_save() {
        RegistrationRequest request = new RegistrationRequest("Test167", "Testov@#fwq", "test34324fdwf@test.com", "test 123 456", "+45600000000321");
        int id = customerService.save(request).getId();
        Optional<Customer> customerRepositoryById = customerRepository.findById(id);

        assertTrue(customerRepositoryById.isPresent());
        assertEquals(customerRepositoryById.get().getFirstName(), request.getName());
        assertEquals(customerRepositoryById.get().getLastName(), request.getSurname());
        assertEquals(customerRepositoryById.get().getEmail(), request.getEmail());
        assertEquals(customerRepositoryById.get().getAddress(), request.getAddress());
        assertEquals(customerRepositoryById.get().getPhone(), request.getPhone());
    }
}