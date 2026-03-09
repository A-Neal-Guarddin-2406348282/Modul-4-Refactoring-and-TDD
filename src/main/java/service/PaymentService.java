package service;

import model.Order;
import model.Payment;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    Payment addPayment(Order order, String method, Map<String, String> paymentData);

    Payment setStatus(Payment payment, String status);

    Payment getPayment(String paymentId);

    List<Payment> getAllPayments();
}
