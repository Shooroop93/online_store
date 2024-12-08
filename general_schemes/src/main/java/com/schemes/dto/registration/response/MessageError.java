package com.schemes.dto.registration.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MessageError {

    @JsonProperty("error_list")
    private List<String> errorList;

    public MessageError(List<String> errorList) {
        this.errorList = errorList;
    }
}