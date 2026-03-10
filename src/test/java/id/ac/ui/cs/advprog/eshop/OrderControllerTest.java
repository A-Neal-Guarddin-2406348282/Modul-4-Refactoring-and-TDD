package id.ac.ui.cs.advprog.eshop;

import id.ac.ui.cs.advprog.eshop.controller.OrderController;
import id.ac.ui.cs.advprog.eshop.model.Product;
import model.Order;
import model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import service.OrderService;
import service.PaymentService;
import service.PaymentServiceImpl;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService orderService;

    @MockitoBean
    PaymentService paymentService;

    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Keyboard");
        product.setProductQuantity(2);

        order = new Order("order-1", List.of(product), System.currentTimeMillis(), "Neal");
        payment = new Payment(
                "payment-1",
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                PaymentServiceImpl.SUCCESS,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );
    }

    @Test
    void testCreateOrderPage() throws Exception {
        mockMvc.perform(get("/order/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderCreate"));
    }

    @Test
    void testCreateOrderPost() throws Exception {
        mockMvc.perform(post("/order/create")
                        .param("author", "Neal")
                        .param("productName", "Keyboard")
                        .param("productQuantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(org.hamcrest.Matchers.startsWith("redirect:/order/pay/")));

        verify(orderService).createOrder(any(Order.class));
    }

    @Test
    void testOrderHistoryPage() throws Exception {
        mockMvc.perform(get("/order/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"));
    }

    @Test
    void testOrderHistoryPost() throws Exception {
        when(orderService.findAllByAuthor("Neal")).thenReturn(List.of(order));

        mockMvc.perform(post("/order/history").param("author", "Neal"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderHistory"))
                .andExpect(model().attributeExists("author"))
                .andExpect(model().attributeExists("orders"));

        verify(orderService).findAllByAuthor("Neal");
    }

    @Test
    void testPayOrderPageFound() throws Exception {
        when(orderService.findById("order-1")).thenReturn(order);

        mockMvc.perform(get("/order/pay/order-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("orderPay"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void testPayOrderPageNotFound() throws Exception {
        when(orderService.findById("missing")).thenReturn(null);

        mockMvc.perform(get("/order/pay/missing"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/order/history"));
    }

    @Test
    void testPayOrderPostVoucher() throws Exception {
        when(orderService.findById("order-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq(PaymentServiceImpl.VOUCHER_CODE), any(Map.class))).thenReturn(payment);

        mockMvc.perform(post("/order/pay/order-1")
                        .param("method", PaymentServiceImpl.VOUCHER_CODE)
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentResult"))
                .andExpect(model().attributeExists("payment"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void testPayOrderPostBankTransfer() throws Exception {
        Payment bankPayment = new Payment(
                "payment-2",
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                PaymentServiceImpl.SUCCESS,
                Map.of("bankName", "BCA", "referenceCode", "REF001")
        );

        when(orderService.findById("order-1")).thenReturn(order);
        when(paymentService.addPayment(eq(order), eq(PaymentServiceImpl.BANK_TRANSFER), any(Map.class))).thenReturn(bankPayment);

        mockMvc.perform(post("/order/pay/order-1")
                        .param("method", PaymentServiceImpl.BANK_TRANSFER)
                        .param("bankName", "BCA")
                        .param("referenceCode", "REF001"))
                .andExpect(status().isOk())
                .andExpect(view().name("paymentResult"))
                .andExpect(model().attributeExists("payment"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    void testPayOrderPostOrderNotFound() throws Exception {
        when(orderService.findById("missing")).thenReturn(null);

        mockMvc.perform(post("/order/pay/missing")
                        .param("method", PaymentServiceImpl.VOUCHER_CODE)
                        .param("voucherCode", "ESHOP1234ABC5678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/order/history"));
    }
}