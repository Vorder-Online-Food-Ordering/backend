package com.example.backend.service;

import com.example.backend.config.VNPAYConfig;
import com.example.backend.model.Order;
import com.example.backend.response.PaymentResponse;
import com.example.backend.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final VNPAYConfig vnPayConfig;

    @Override
    public PaymentResponse createVNPayment(HttpServletRequest req, Long totalPrice, String bankCode) {
//        long amount = Integer.parseInt(req.getParameter("amount")) * 100L;
        long amount = Integer.parseInt(String.valueOf(totalPrice + "0000")) * 100L;
//        String bankCode = req.getParameter("bankCode");

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(req));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return new PaymentResponse("ok", "success", paymentUrl);

    }
}
