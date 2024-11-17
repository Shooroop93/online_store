package com.example.order_management_system.dto.items.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequest {

    @NotNull(message = "{application.dto.notNull}")
    @JsonProperty("owner_ID")
    private Long ownerId;

    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    @JsonProperty("item_name")
    private String itemName;

    @NotNull(message = "{application.dto.notNull}")
    @NotEmpty(message = "{application.dto.notEmpty}")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "{application.dto.notNull}")
    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("quantity")
    private int quantity;
}
