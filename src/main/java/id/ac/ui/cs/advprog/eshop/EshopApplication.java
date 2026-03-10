package id.ac.ui.cs.advprog.eshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {
        "id.ac.ui.cs.advprog.eshop",
        "model",
        "repository",
        "service",
        "enums"
})
public class EshopApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(EshopApplication.class, args);

        // Untuk keperluan unit test/coverage: kalau property ini true, langsung close context
        if (Boolean.getBoolean("app.closeAfterRun")) {
            ctx.close();
        }
    }

}
