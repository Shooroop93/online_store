package com.example.order_management_system.controller;

import com.example.order_management_system.dto.registration.request.RegistrationRequest;
import com.example.order_management_system.model.Customer;
import com.example.order_management_system.service.CustomerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomController.class)
class CustomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private RegistrationRequest registrationRequest;

    private String testRequest = """
            {
                "name" : "test",
                "surname" : "testov",
                "phone" : "+700000000001",
                "email" : "testov@test.test",
                "address" : "testov"
            }
            """;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        registrationRequest = new ObjectMapper().readValue(testRequest, RegistrationRequest.class);
    }

    @Test
    void saveCustomer_valid() throws Exception {
        when(customerService.save(registrationRequest)).thenReturn(new Customer(1, "t", "e", "s@t.ru", "+700000000001", "test"));
        mockMvc.perform(post("/api/v1/store/reg").contentType(MediaType.APPLICATION_JSON).content(testRequest))
                .andExpect(status().isCreated());
    }

    @Test
    void saveCustomer_no_valid_name() throws Exception {
        registrationRequest.setName(null);
        mockMvc.perform(post("/api/v1/store/reg").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCustomer_no_valid_surname() throws Exception {
        registrationRequest.setSurname(null);
        mockMvc.perform(post("/api/v1/store/reg").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCustomer_no_valid_phone() throws Exception {
        registrationRequest.setPhone(null);
        mockMvc.perform(post("/api/v1/store/reg").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCustomer_no_valid_email() throws Exception {
        registrationRequest.setEmail(null);
        mockMvc.perform(post("/api/v1/store/reg").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveCustomer_no_valid_address() throws Exception {
        registrationRequest.setAddress(null);
        mockMvc.perform(post("/api/v1/store/reg").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }
}