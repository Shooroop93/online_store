package com.schemes.dto.registration.request;

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
    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    private String name;

    @Length(min = 1, max = 30)
    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    private String surname;

    @NotNull(message = "{application.dto.notNull}")
    @Email
    private String email;

    @NotNull(message = "{application.dto.notNull}")
    private String address;

    @NotNull(message = "{application.dto.notNull}")
    @Pattern(regexp = "^\\+\\d{1,3}\\s?\\(?\\d{1,4}\\)?[\\s.-]?\\d{1,4}[\\s.-]?\\d{1,4}[\\s.-]?\\d{1,9}$")
    private String phone;
}