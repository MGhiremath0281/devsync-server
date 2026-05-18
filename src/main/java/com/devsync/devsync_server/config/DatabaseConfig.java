package com.devsync.devsync_server.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:postgresql://localhost:5432/devsync");
        config.setUsername("postgres");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000); // 5 minutes
        config.setConnectionTimeout(2000); // 2 seconds fast-fail
        config.setMaxLifetime(1800000); // 30 minutes

        config.setPoolName("DevSyncHikariPool");

        return new HikariDataSource(config);
    }
}