package com.example.controller;

import com.example.order_management_system.controller.DefaultController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DefaultController.class)
@ContextConfiguration(classes = {DefaultController.class})
public class DefaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${application.notices.health}")
    private String healthMessage;

    @Test
    public void checkMessageForApplacationYamlTest() throws Exception {
        mockMvc.perform(get("/api/v1/store/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(healthMessage));
    }

    @Test
    public void checkConstantMessageTest() throws Exception {
        mockMvc.perform(get("/api/v1/store/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }
}