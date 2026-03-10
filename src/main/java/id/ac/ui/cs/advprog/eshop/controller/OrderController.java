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

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage() {
        return "orderCreate";
    }

    @PostMapping("/create")
    public String createOrderPost(@RequestParam("author") String author,
                                  @RequestParam("productName") String productName,
                                  @RequestParam("productQuantity") int productQuantity) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName(productName);
        product.setProductQuantity(productQuantity);

        Order order = new Order(
                UUID.randomUUID().toString(),
                List.of(product),
                System.currentTimeMillis(),
                author
        );

        orderService.createOrder(order);
        return "redirect:/order/pay/" + order.getId();
    }

    @GetMapping("/history")
    public String orderHistoryPage() {
        return "orderHistory";
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam("author") String author, Model model) {
        model.addAttribute("author", author);
        model.addAttribute("orders", orderService.findAllByAuthor(author));
        return "orderHistory";
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable("orderId") String orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }

        model.addAttribute("order", order);
        return "orderPay";
    }

    @PostMapping("/pay/{orderId}")
    public String payOrderPost(@PathVariable("orderId") String orderId,
                               @RequestParam("method") String method,
                               @RequestParam(value = "voucherCode", required = false) String voucherCode,
                               @RequestParam(value = "bankName", required = false) String bankName,
                               @RequestParam(value = "referenceCode", required = false) String referenceCode,
                               Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }

        Map<String, String> paymentData;
        if (PaymentServiceImpl.VOUCHER_CODE.equals(method)) {
            paymentData = Map.of("voucherCode", voucherCode == null ? "" : voucherCode);
        } else {
            paymentData = Map.of(
                    "bankName", bankName == null ? "" : bankName,
                    "referenceCode", referenceCode == null ? "" : referenceCode
            );
        }

        Payment payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("payment", payment);
        model.addAttribute("order", payment.getOrder());

        return "paymentResult";
    }
}