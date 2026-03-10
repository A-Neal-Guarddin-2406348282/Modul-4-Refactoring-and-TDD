package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import model.Order;
import model.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.OrderService;
import service.PaymentService;
import service.PaymentServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// Class ini sudah disesuaikan dengan refactoring dan lebih rapih
@Controller
@RequestMapping("/order")
public class OrderController {

    // List file HTML
    private static final String REDIRECT_ORDER_HISTORY = "redirect:/order/history";
    private static final String ORDER_HISTORY_VIEW = "orderHistory";
    private static final String ORDER_CREATE_VIEW = "orderCreate";
    private static final String ORDER_PAY_VIEW = "orderPay";
    private static final String PAYMENT_RESULT_VIEW = "paymentResult";

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage() {
        return ORDER_CREATE_VIEW;
    }

    @PostMapping("/create")
    public String createOrderPost(@RequestParam("author") String author,
                                  @RequestParam("productName") String productName,
                                  @RequestParam("productQuantity") int productQuantity) {
        Order order = buildOrder(author, productName, productQuantity);
        this.orderService.createOrder(order);
        return "redirect:/order/pay/" + order.getId();
    }

    @GetMapping("/history")
    public String orderHistoryPage() {
        return ORDER_HISTORY_VIEW;
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam("author") String author, Model model) {
        model.addAttribute("author", author);
        model.addAttribute("orders", this.orderService.findAllByAuthor(author));
        return ORDER_HISTORY_VIEW;
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable("orderId") String orderId, Model model) {
        Order order = this.orderService.findById(orderId);
        if (order == null) {
            return REDIRECT_ORDER_HISTORY;
        }

        model.addAttribute("order", order);
        return ORDER_PAY_VIEW;
    }

    @PostMapping("/pay/{orderId}")
    public String payOrderPost(@PathVariable("orderId") String orderId,
                               @RequestParam("method") String method,
                               @RequestParam(value = "voucherCode", required = false) String voucherCode,
                               @RequestParam(value = "bankName", required = false) String bankName,
                               @RequestParam(value = "referenceCode", required = false) String referenceCode,
                               Model model) {
        Order order = this.orderService.findById(orderId);
        if (order == null) {
            return REDIRECT_ORDER_HISTORY;
        }

        Map<String, String> paymentData =buildPaymentData(method, voucherCode, bankName, referenceCode);


        Payment payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("payment", payment);
        model.addAttribute("order", payment.getOrder());

        return PAYMENT_RESULT_VIEW;
    }

    private Order buildOrder(String author, String productName, int productQuantity) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName(productName);
        product.setProductQuantity(productQuantity);

        return new Order(
                UUID.randomUUID().toString(),
                List.of(product),
                System.currentTimeMillis(),
                author
        );
    }

    private Map<String, String> buildPaymentData(String method, String voucherCode, String bankName, String referenceCode) {
        if (PaymentServiceImpl.VOUCHER_CODE.equals(method)) {
            return Map.of("voucherCode", defaultValue(voucherCode));
        }
        return Map.of(
                "bankName", defaultValue(bankName),
                "referenceCode", defaultValue(referenceCode)
        );

    }

    private String defaultValue(String value) {
        return value == null ? "" : value;
    }

}
