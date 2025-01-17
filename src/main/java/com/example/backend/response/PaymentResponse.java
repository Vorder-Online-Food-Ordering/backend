package com.example.backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PaymentResponse {
    private String code;
    private String message;
    private String payment_url;
}
