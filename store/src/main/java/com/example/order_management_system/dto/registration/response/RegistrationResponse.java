package com.example.order_management_system.dto.registration.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationResponse {

    private int id = -1;
    private List<Participant> participants;
    private String message;
    @JsonProperty("error")
    private MessageError error;

    public RegistrationResponse(MessageError error) {
        this.error = error;
    }

    public RegistrationResponse(int id, String message) {
        this.id = id;
        this.message = message;
    }
}