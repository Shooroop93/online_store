package com.example.order_management_system.dto.registration.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    @Length(min = 1, max = 30)
    @NotNull(message = "не должно равняться null")
    @NotEmpty(message = "не должно быть пустым")
    private String name;

    @Length(min = 1, max = 30)
    @NotNull(message = "не должно равняться null")
    @NotEmpty(message = "не должно быть пустым")
    private String surname;

    @NotNull(message = "не должно равняться null")
    @Email
    private String email;

    @NotNull(message = "не должно равняться null")
    private String address;

    @NotNull(message = "не должно равняться null")
    @Pattern(regexp = "^\\+\\d{1,3}\\s?\\(?\\d{1,4}\\)?[\\s.-]?\\d{1,4}[\\s.-]?\\d{1,4}[\\s.-]?\\d{1,9}$")
    private String phone;
}