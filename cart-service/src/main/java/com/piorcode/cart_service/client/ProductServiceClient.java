package com.piorcode.cart_service.client;

import com.piorcode.cart_service.api.ProductResponse;
import com.piorcode.cart_service.exception.ProductNotFoundException;
import com.piorcode.cart_service.exception.ProductServiceUnavailableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class ProductServiceClient {

    private final RestClient productServiceRestClient;

    public ProductServiceClient(RestClient productServiceRestClient) {
        this.productServiceRestClient = productServiceRestClient;
    }

    public ProductResponse getProduct(UUID productId) {
        try {
            return productServiceRestClient
                .get()
                .uri("/api/v1/products/{productId}", productId)
                .retrieve()
                .body(ProductResponse.class);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new ProductNotFoundException(productId);
        } catch (RestClientException exception) {
            throw new ProductServiceUnavailableException();
        }
    }
}
