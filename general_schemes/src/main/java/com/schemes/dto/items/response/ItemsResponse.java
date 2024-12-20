package com.schemes.dto.items.response;

import com.schemes.dto.registration.response.MessageError;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemsResponse {

    private int id = -1;
    private String message;
    private List<Item> itemList;
    private MessageError error;

    public ItemsResponse(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public void addItem(Item item) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.add(item);
    }

    public void addError(String errorMessage) {
        if (this.error == null) {
            this.error = new MessageError(new ArrayList<>());
        }
        this.error.getErrorList().add(errorMessage);
    }
}