package org.hotel.backend.controller;



import com.paypal.api.payments.Links;
//import com.paypal.api.payments.Order;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import lombok.extern.slf4j.Slf4j;
import org.hotel.backend.dto.Order;
import org.hotel.backend.service.PaypalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/paypal")
@Slf4j
public class PayPalController {

    @Autowired
    PaypalService paypalService;

    public static final String SUCCESS_URL = "/api/paypal/pay/success";
    public static final String CANCEL_URL = "/api/paypal/pay/cancel";


    @GetMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/pay")
    public String payment(@ModelAttribute("order") Order order) {
        try {
            Payment payment = paypalService.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(), order.getIntent(), order.getDescription(), "http://localhost:8080" + CANCEL_URL, "http://localhost:8080" + SUCCESS_URL);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }

        } catch (PayPalRESTException e) {
            return e.getMessage();
        }
        return "redirect:/";
    }

    @GetMapping("/pay/cancel")
    public String cancelPay() {
        return "cancel";
    }

    @GetMapping("/pay/success")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                return "success";
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/";
    }
}