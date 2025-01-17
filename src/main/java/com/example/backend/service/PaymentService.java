package com.example.backend.service;

import com.example.backend.response.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
     PaymentResponse createVNPayment(HttpServletRequest req, Long totalPrice, String bankCode);
}
