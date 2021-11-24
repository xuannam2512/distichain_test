package com.distichain.productsynchronizer.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Product {
    private String sku;
    private String title;
    private String description;
    private BigDecimal price;
    private Long quantity;
}
