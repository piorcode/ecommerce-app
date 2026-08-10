package com.piorcode.cart_service.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ProductServiceProperties.class)
public class ProductServiceClientConfiguration {

    @Bean
    RestClient productServiceRestClient(RestClient.Builder builder, ProductServiceProperties properties) {
        return builder.baseUrl(properties.baseUrl()).build();
    }
}