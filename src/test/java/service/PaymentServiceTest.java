package service;

import enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Product;
import model.Order;
import model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.OrderRepository;
import repository.PaymentRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo");
        product.setProductQuantity(2);

        this.order = new Order("order-1", List.of(product), 1708560000L, "Safira");
    }

    // Tes Voucher valid dimulai disini

    @Test
    void testAddPaymentVoucherValid() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );

        assertEquals(PaymentServiceImpl.SUCCESS, result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
        verify(paymentRepository).save(result);
        verify(orderRepository).save(order);
    }

    @Test
    void testAddPaymentVoucherInvalid() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                Map.of("voucherCode", "ABC")
        );

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
        verify(paymentRepository).save(result);
        verify(orderRepository).save(order);
    }

    // Test untuk Bank Transfer Payment disini
    @Test
    void testAddPaymentBankTransferValid() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                Map.of("bankName", "BCA", "referenceCode", "REF001")
        );

        assertEquals(PaymentServiceImpl.SUCCESS, result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
        verify(paymentRepository).save(result);
        verify(orderRepository).save(order);
    }

    @Test
    void testAddPaymentBankTransferInvalidBecauseBankNameEmpty() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                Map.of("bankName", "", "referenceCode", "REF001")
        );

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferInvalidBecauseReferenceCodeEmpty() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                Map.of("bankName", "BCA", "referenceCode", "")
        );

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testSetStatusSuccess() {
        Payment payment = new Payment(
                "payment-1",
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                PaymentServiceImpl.REJECTED,
                Map.of("voucherCode", "ABC")
        );

        Payment result = paymentService.setStatus(payment, PaymentServiceImpl.SUCCESS);

        assertEquals(PaymentServiceImpl.SUCCESS, result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), order.getStatus());
        verify(paymentRepository).save(payment);
        verify(orderRepository).save(order);
    }

    @Test
    void testSetStatusRejected() {
        Payment payment = new Payment(
                "payment-1",
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                PaymentServiceImpl.SUCCESS,
                Map.of("bankName", "BCA", "referenceCode", "REF001")
        );

        Payment result = paymentService.setStatus(payment, PaymentServiceImpl.REJECTED);

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
        verify(paymentRepository).save(payment);
        verify(orderRepository).save(order);
    }

    @Test
    void testSetStatusWhenPaymentNull() {
        Payment result = paymentService.setStatus(null, PaymentServiceImpl.SUCCESS);

        assertNull(result);
        verify(paymentRepository, never()).save(org.mockito.ArgumentMatchers.any());
        verify(orderRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void testGetPaymentIfFound() {
        Payment payment = new Payment(
                "payment-1",
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                PaymentServiceImpl.SUCCESS,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );

        when(paymentRepository.findById("payment-1")).thenReturn(payment);

        Payment result = paymentService.getPayment("payment-1");
        assertEquals(payment, result);
    }

    @Test
    void testGetPaymentIfNotFound() {
        when(paymentRepository.findById("not-found")).thenReturn(null);

        assertNull(paymentService.getPayment("not-found"));
    }

    @Test
    void testGetAllPayments() {
        // Kasih enum
        Payment payment1 = new Payment(
                "payment-1",
                order,
                PaymentServiceImpl.VOUCHER_CODE,
                PaymentServiceImpl.SUCCESS,
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );

        Payment payment2 = new Payment(
                "payment-2",
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                PaymentServiceImpl.REJECTED,
                Map.of("bankName", "BCA", "referenceCode", "REF001")
        );

        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));

        List<Payment> results = paymentService.getAllPayments();
        assertEquals(2, results.size());
    }

    @Test
    void testAddPaymentBankTransferInvalidBecausePaymentDataNull() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                null
        );

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferInvalidBecauseBankNameBlank() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                Map.of("bankName", "   ", "referenceCode", "REF001")
        );

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }

    @Test
    void testAddPaymentBankTransferInvalidBecauseReferenceCodeBlank() {
        Payment result = paymentService.addPayment(
                order,
                PaymentServiceImpl.BANK_TRANSFER,
                Map.of("bankName", "BCA", "referenceCode", "   ")
        );

        assertEquals(PaymentServiceImpl.REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), order.getStatus());
    }
}
