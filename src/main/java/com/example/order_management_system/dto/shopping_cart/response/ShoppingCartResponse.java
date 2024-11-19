package com.example.order_management_system.dto.shopping_cart.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartResponse {

    @JsonProperty("item_list")
    private List<Item> itemList;
    private String message;
    private MessageError error;

    public void addError(String message){
        if (this.error == null) {
            this.error = new MessageError(new ArrayList<>());
        }
        this.error.getErrorList().add(message);
    }
}