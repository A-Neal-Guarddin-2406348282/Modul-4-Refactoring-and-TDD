package model;

import enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    private Order order;

    @BeforeEach
    void setUp() {
        Product product1 = new Product();
        product1.setProductId("product-1");
        product1.setProductName("Sampo");
        product1.setProductQuantity(2);

        this.order = new Order("order-1", List.of(product1), 1708560000L, "Neal");
    }

    @Test
    void testCreatePayment() {
        Map<String, String> paymentData = Map.of("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("payment-1", order, "Voucher Code", OrderStatus.SUCCESS.getValue(), paymentData);

        assertEquals("payment-1", payment.getId());
        assertEquals(order, payment.getOrder());
        assertEquals("Voucher Code", payment.getMethod());
        assertEquals(OrderStatus.SUCCESS.getValue(), payment.getStatus());
        assertEquals("ESHOP1234ABC5678", payment.getPaymentData().get("voucherCode"));
    }
}
