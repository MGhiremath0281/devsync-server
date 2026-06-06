package com.devsync.devsync_server.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ServerHttpObservationFilter;

@Configuration
public class TelemetryConfig {

    @Bean
    public ServerHttpObservationFilter httpObservationFilter(ObservationRegistry registry) {
        return new ServerHttpObservationFilter(registry);
    }

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry registry) {
        return new ObservedAspect(registry);
    }
}