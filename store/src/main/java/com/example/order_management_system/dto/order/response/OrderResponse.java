package com.example.order_management_system.dto.order.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {

    @NotNull(message = "{application.dto.notNull}")
    @JsonProperty("id_owner_shopping_cart")
    private int idOwnerShoppingCart;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("order_number")
    private int orderNumber;

    private String status;

    @JsonProperty("item_list")
    private List<OrderItemResponse> itemList;
    private String message;
    private MessageError error;

    public OrderResponse(int idOwnerShoppingCart) {
        this.idOwnerShoppingCart = idOwnerShoppingCart;
    }

    public void addError(String message) {
        if (this.error == null) {
            this.error = new MessageError(new ArrayList<>());
        }
        this.error.getErrorList().add(message);
    }

    public void addItem(OrderItemResponse item) {
        if (this.itemList == null) {
            this.itemList = new ArrayList<>();
        }
        this.itemList.add(item);
    }
}