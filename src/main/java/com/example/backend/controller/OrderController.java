package com.example.backend.controller;


import com.example.backend.model.Order;
import com.example.backend.model.User;
import com.example.backend.request.OrderRequest;
import com.example.backend.response.PaymentResponse;
import com.example.backend.service.OrderService;
import com.example.backend.service.PaymentService;
import com.example.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    private OrderService orderService;
    private UserService userService;
    private PaymentService paymentService;

    @Autowired
    public OrderController(OrderService orderService, PaymentService paymentService, UserService userService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.userService = userService;
    }


//
//    @GetMapping("/vn-pay-callback")
//    public ResponseEntity<PaymentResponse> payCallbackHandler( HttpServletResponse response, @RequestParam String vnp_ResponseCode ) {
////        String status = request.getParameter("vnp_ResponseCode");
//
//        // get params from vnpay
//        // respnse.sendRedirect("link frontend") // redirect to fe link => 00 => success
//        // in fe call api confrim order => save order info to db
//        if (vnp_ResponseCode.equals("00")) {
//            response.sendRedirect("http://your-frontend-domain/payment-success");
//            PaymentResponse res = new PaymentResponse("00", "Success", "");
//            return new ResponseEntity<>(res,HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }

@GetMapping("/vn-pay-callback")
public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response, @RequestParam String vnp_ResponseCode) throws IOException {

    if ("00".equals(vnp_ResponseCode)) {
        response.sendRedirect("http://localhost:3000/payment-success");

    } else {
        response.sendRedirect("http://localhost:3000/payment-failure");
    }
}


    @PostMapping("/order")
    public ResponseEntity<PaymentResponse> createOrder(@RequestBody OrderRequest req,
                                                       @RequestHeader("Authorization") String jwt,
                                                       HttpServletRequest request
                                                      ) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order order =  orderService.createOrder(req, user);

        String bankCode = "NCB";


        PaymentResponse res = paymentService.createVNPayment(request, order.getTotalPrice(), bankCode);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }


    @GetMapping("/order/user")
    public ResponseEntity<List<Order>> getOrderHistory(@RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.getUserOrders(user.getId());

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

}
