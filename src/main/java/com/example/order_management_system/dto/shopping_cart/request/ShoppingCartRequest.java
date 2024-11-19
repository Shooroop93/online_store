package com.example.order_management_system.dto.shopping_cart.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingCartRequest {

    @JsonProperty("id_owner_shopping_cart")
    private int idOwnerShoppingCart;

    @JsonProperty("item_list")
    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    private List<Item> itemsList;
}