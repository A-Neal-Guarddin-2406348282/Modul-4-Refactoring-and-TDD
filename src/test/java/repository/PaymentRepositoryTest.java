package repository;

import enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Product;
import model.Order;
import model.Payment;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {
    PaymentRepository paymentRepository;
    Payment payment1;
    Payment payment2;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sampo");
        product.setProductQuantity(2);

        Order order = new Order("order-1", List.of(product), 1708560000L, "Safira");

        payment1 = new Payment(
                "payment-1",
                order,
                "Voucher Code",
                "SUCCESS",
                Map.of("voucherCode", "ESHOP1234ABC5678")
        );

        payment2 = new Payment(
                "payment-2",
                order,
                "Bank Transfer",
                "REJECTED",
                Map.of("bankName", "BCA", "referenceCode", "REF001")
        );
    }

    @Test
    void testSaveCreate() {
        Payment result = paymentRepository.save(payment1);

        Payment findResult = paymentRepository.findById(payment1.getId());
        assertEquals(payment1.getId(), result.getId());
        assertEquals(payment1.getId(), findResult.getId());
        assertEquals(payment1.getMethod(), findResult.getMethod());
        assertEquals(payment1.getStatus(), findResult.getStatus());
    }

    @Test
    void testSaveUpdate() {
        paymentRepository.save(payment1);

        Payment updatedPayment = new Payment(
                "payment-1",
                payment1.getOrder(),
                "Voucher Code",
                OrderStatus.FAILED.getValue(),
                Map.of("voucherCode", "INVALID")
        );

        Payment result = paymentRepository.save(updatedPayment);
        Payment findResult = paymentRepository.findById("payment-1");

        assertEquals("payment-1", result.getId());
        assertEquals(OrderStatus.FAILED.getValue(), findResult.getStatus());
        assertEquals("INVALID", findResult.getPaymentData().get("voucherCode"));
    }

    @Test
    void testFindByIdIfFound() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        Payment findResult = paymentRepository.findById("payment-2");
        assertEquals("payment-2", findResult.getId());
        assertEquals("Bank Transfer", findResult.getMethod());
    }

    @Test
    void testFindByIdIfNotFound() {
        assertNull(paymentRepository.findById("not-found"));
    }

    @Test
    void testFindAll() {
        paymentRepository.save(payment1);
        paymentRepository.save(payment2);

        assertEquals(2, paymentRepository.findAll().size());
    }
}
