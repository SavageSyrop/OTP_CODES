package ru.otp.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayInitializer {
    @Value("${flyway.url}")
    private String url;
    @Value("${flyway.user}")
    private String username;
    @Value("${flyway.password}")
    private String password;

    public void initialize() {
        Flyway.configure()
                .dataSource(
                        url,
                        username,
                        password
                )
                .cleanDisabled(true)
                .locations("classpath:db.migration")
                .load().migrate();
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            // do nothing
        };
    }
}
