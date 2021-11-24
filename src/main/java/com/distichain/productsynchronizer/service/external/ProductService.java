package com.distichain.productsynchronizer.service.external;

import com.distichain.productsynchronizer.domain.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    public Optional<Product> getBySku(String sku) {
        return Optional.empty();
    }

    public void create(Product product) {}

    public void update(String sku, Product product) {}
}
