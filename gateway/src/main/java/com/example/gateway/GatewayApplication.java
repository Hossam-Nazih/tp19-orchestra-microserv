package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    // Steps 6 and 9: routing by service name through Eureka
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.discovery.enabled", havingValue = "true", matchIfMissing = true)
    RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(r -> r.path("/clients/**", "/client/**").uri("lb://SERVICE-CLIENT"))
            .route(r -> r.path("/voitures/**").uri("lb://SERVICE-VOITURE"))
            .build();
    }

    // Dynamic routing: every service registered in Eureka is reachable by name,
    // e.g. http://localhost:8888/SERVICE-CLIENT/clients
    @Bean
    @ConditionalOnProperty(name = "spring.cloud.discovery.enabled", havingValue = "true", matchIfMissing = true)
    DiscoveryClientRouteDefinitionLocator routesDynamique(
            ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp) {
        return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
    }
}
