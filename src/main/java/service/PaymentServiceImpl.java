package service;

import enums.OrderStatus;
import model.Order;
import model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.OrderRepository;
import repository.PaymentRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    public static final String VOUCHER_CODE = "Voucher Code";
    public static final String BANK_TRANSFER = "Bank Transfer";
    public static final String SUCCESS = "SUCCESS";
    public static final String REJECTED = "REJECTED";

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                order,
                method,
                REJECTED,
                paymentData
        );

        // Voucher Payment Implementation
        if (VOUCHER_CODE.equals(method)) {
            String voucherCode = paymentData == null ? null : paymentData.get("voucherCode");
            if (isVoucherValid(voucherCode)) {
                payment.setStatus(SUCCESS);
            }
        } else if (BANK_TRANSFER.equals(method)) {
            if (isBankTransferValid(paymentData)) {
                payment.setStatus(SUCCESS);
            }
        }

        synchronizeOrderStatus(payment);
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        if (payment == null) {
            return null;
        }

        payment.setStatus(status);
        synchronizeOrderStatus(payment);
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    private void synchronizeOrderStatus(Payment payment) {
        if (payment.getOrder() == null) {
            return;
        }

        if (SUCCESS.equals(payment.getStatus())) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (REJECTED.equals(payment.getStatus())) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }

        orderRepository.save(payment.getOrder());
    }

    // Refactoring isVoucherValid()
    private boolean isVoucherValid(String voucherCode) {
        if (voucherCode == null) {
            return false;
        }

        if (voucherCode.length() != 16) {
            return false;
        }

        if (!voucherCode.startsWith("ESHOP")) {
            return false;
        }

        long digitCount = voucherCode.chars().filter(Character::isDigit).count();
        return digitCount == 8;
    }

    // Payment validation berhasil
    private boolean isBankTransferValid(Map<String, String> paymentData) {
        if (paymentData == null) {
            return false;
        }

        String bankName = paymentData.get("bankName");
        String referenceCode = paymentData.get("referenceCode");

        return hasText(bankName) && hasText(referenceCode);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}