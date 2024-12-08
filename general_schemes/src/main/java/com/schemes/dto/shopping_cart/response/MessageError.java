package com.schemes.dto.shopping_cart.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageError {

    @JsonProperty("error_list")
    private List<String> errorList;

    public MessageError(List<String> errorList) {
        this.errorList = errorList;
    }
}