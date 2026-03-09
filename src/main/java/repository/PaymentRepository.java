//package repository;
//
//import model.Payment;
//import org.springframework.stereotype.Repository;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Repository
//public class PaymentRepository {
//    private List<Payment> paymentData = new ArrayList<>();
//
//    // Update payment
//    public Payment save(Payment payment) {
//        int i = 0;
//        for (Payment savedPayment : paymentData) {
//            if (savedPayment.getId().equals(payment.getId())) {
//                paymentData.remove(i);
//                paymentData.add(i, payment);
//                return payment;
//            }
//            i += 1;
//        }
//
//        paymentData.add(payment);
//        return payment;
//    }
//
//    public Payment findById(String paymentId) {
//        for (Payment savedPayment : paymentData) {
//            if (savedPayment.getId().equals(paymentId)) {
//                return savedPayment;
//            }
//        }
//
//        return null;
//    }
//
//    public List<Payment> findAll() {
//        return new ArrayList<>(paymentData);
//    }
//}
