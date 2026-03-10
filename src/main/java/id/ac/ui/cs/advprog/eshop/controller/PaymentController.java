package id.ac.ui.cs.advprog.eshop.controller;

import model.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.PaymentService;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private static final String PAYMENT_DETAIL_FORM_VIEW = "paymentDetailForm";
    private static final String PAYMENT_DETAIL_VIEW = "paymentDetail";
    private static final String PAYMENT_ADMIN_LIST_VIEW = "paymentAdminList";
    private static final String PAYMENT_ADMIN_DETAIL_VIEW = "paymentAdminDetail";
    private static final String REDIRECT_PAYMENT_DETAIL = "redirect:/payment/detail";
    private static final String REDIRECT_PAYMENT_ADMIN_LIST = "redirect:/payment/admin/list";
    private static final String REDIRECT_PAYMENT_ADMIN_DETAIL_PREFIX = "redirect:/payment/admin/detail/";

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/detail")
    public String paymentDetailFormPage() {
        return PAYMENT_DETAIL_FORM_VIEW;
    }

    @GetMapping("/detail/{paymentId}")
    public String paymentDetailPage(@PathVariable("paymentId") String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return REDIRECT_PAYMENT_DETAIL;
        }

        model.addAttribute("payment", payment);
        return PAYMENT_DETAIL_VIEW;
    }

    @GetMapping("/admin/list")
    public String paymentAdminListPage(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        return PAYMENT_ADMIN_LIST_VIEW;
    }

    @GetMapping("/admin/detail/{paymentId}")
    public String paymentAdminDetailPage(@PathVariable("paymentId") String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return REDIRECT_PAYMENT_ADMIN_LIST;
        }

        model.addAttribute("payment", payment);
        return PAYMENT_ADMIN_DETAIL_VIEW;
    }

    @PostMapping("/admin/set-status/{paymentId}")
    public String paymentAdminSetStatus(@PathVariable("paymentId") String paymentId,
                                        @RequestParam("status") String status) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return REDIRECT_PAYMENT_ADMIN_LIST;
        }

        paymentService.setStatus(payment, status);
        return REDIRECT_PAYMENT_ADMIN_DETAIL_PREFIX + paymentId;
    }
}