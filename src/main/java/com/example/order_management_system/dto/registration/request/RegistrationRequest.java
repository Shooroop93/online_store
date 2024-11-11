package com.example.order_management_system.dto.registration.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegistrationRequest {

    @Length(min = 1, max = 30)
    @NotNull
    @NotEmpty
    private String name;

    @Length(min = 1, max = 30)
    @NotNull
    @NotEmpty
    private String surname;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String address;

    @NotNull
    @Pattern(regexp = "^\\+\\d{1,3}\\s?\\(?\\d{1,4}\\)?[\\s.-]?\\d{1,4}[\\s.-]?\\d{1,4}[\\s.-]?\\d{1,9}$")
    private String phone;
}