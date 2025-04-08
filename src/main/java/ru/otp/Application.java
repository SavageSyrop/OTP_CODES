package ru.otp;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.otp.configuration.FlywayInitializer;

@Slf4j
@SpringBootApplication(scanBasePackages = "ru.otp")
@EnableTransactionManagement
@EnableJpaRepositories("ru.otp")
public class Application {
    public static void main(String[] args) {

        log.info("Application stated!");

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        context.getBean(FlywayInitializer.class).initialize();
    }
}
