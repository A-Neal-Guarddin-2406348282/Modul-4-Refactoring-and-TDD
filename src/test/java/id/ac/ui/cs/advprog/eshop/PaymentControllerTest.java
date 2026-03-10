package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.controller.PaymentController;
import id.ac.ui.cs.advprog.eshop.model.Product;
import model.Order;
import model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import service.PaymentService;
import service.PaymentServiceImpl;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Keyboard");
        product.setProductQuantity(2);

        Order order = new Order("order-1", List.of(product), System.currentTimeMillis(), "Neal");
        payment = new Payment(
                "payment-1",
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                PaymentServiceImpl.SUCCESS,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );
    }

    @Test
    void testPaymentDetailFormPage() throws Exception {
        mockMvc.perform(get("/payment/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetailForm"));
    }

    @Test
    void testPaymentDetailPageFound() throws Exception {
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        mockMvc.perform(get("/payment/detail/payment-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void testPaymentDetailPageNotFound() throws Exception {
        when(paymentService.getPayment("missing")).thenReturn(null);

        mockMvc.perform(get("/payment/detail/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/detail"));
    }

    @Test
    void testPaymentAdminListPage() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(List.of(payment));

        mockMvc.perform(get("/payment/admin/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminList"))
                .andExpect(model().attributeExists("payments"));
    }

    @Test
    void testPaymentAdminDetailPageFound() throws Exception {
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        mockMvc.perform(get("/payment/admin/detail/payment-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentAdminDetail"))
                .andExpect(model().attributeExists("payment"));
    }

    @Test
    void testPaymentAdminDetailPageNotFound() throws Exception {
        when(paymentService.getPayment("missing")).thenReturn(null);

        mockMvc.perform(get("/payment/admin/detail/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/admin/list"));
    }

    @Test
    void testPaymentAdminSetStatusSuccess() throws Exception {
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        mockMvc.perform(post("/payment/admin/set-status/payment-1")
                        .param("status", PaymentServiceImpl.SUCCESS))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/admin/detail/payment-1"));

        verify(paymentService).setStatus(payment, PaymentServiceImpl.SUCCESS);
    }

    @Test
    void testPaymentAdminSetStatusRejected() throws Exception {
        when(paymentService.getPayment("payment-1")).thenReturn(payment);

        mockMvc.perform(post("/payment/admin/set-status/payment-1")
                        .param("status", PaymentServiceImpl.REJECTED))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/admin/detail/payment-1"));

        verify(paymentService).setStatus(payment, PaymentServiceImpl.REJECTED);
    }

    @Test
    void testPaymentAdminSetStatusPaymentNotFound() throws Exception {
        when(paymentService.getPayment("missing")).thenReturn(null);

        mockMvc.perform(post("/payment/admin/set-status/missing")
                        .param("status", PaymentServiceImpl.SUCCESS))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/payment/admin/list"));
    }
}