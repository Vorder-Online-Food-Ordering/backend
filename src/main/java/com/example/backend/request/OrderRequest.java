package com.example.backend.request;

import com.example.backend.model.Address;
import lombok.Data;

@Data
public class OrderRequest {
    private Long restaurantId;
    public Address deliveryAddress;

}
