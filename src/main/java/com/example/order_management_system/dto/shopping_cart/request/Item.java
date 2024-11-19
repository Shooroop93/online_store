package com.example.order_management_system.dto.shopping_cart.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {

    @JsonProperty("owner_ID")
    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    private int ownerId;

    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    private int article;

    @JsonProperty("quantity")
    @NotNull(message = "{application.dto.notNull}")
    @Min(0)
    private int quantity;
}