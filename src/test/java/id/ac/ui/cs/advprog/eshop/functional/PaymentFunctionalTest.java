package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void createOrderThenPayWithVoucher(ChromeDriver driver) {
        String author = "Pay-" + UUID.randomUUID();
        String productName = "Item-" + UUID.randomUUID();

        driver.get(baseUrl + "/order/create");
        driver.findElement(By.id("authorInput")).sendKeys(author);
        driver.findElement(By.id("productNameInput")).sendKeys(productName);
        driver.findElement(By.id("productQuantityInput")).sendKeys("1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/order/pay/"));

        driver.findElement(By.id("paymentMethodInput")).sendKeys("Voucher Code");
        driver.findElement(By.id("voucherCodeInput")).sendKeys("ESHOP1234ABC5678");
        driver.findElement(By.id("submitPaymentButton")).click();

        assertTrue(driver.getPageSource().contains("Payment ID"));
    }
}