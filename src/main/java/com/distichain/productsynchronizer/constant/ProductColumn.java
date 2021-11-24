package com.distichain.productsynchronizer.constant;

public enum ProductColumn {
    SKU("sku"),
    TITLE("title"),
    DESCRIPTION("description"),
    PRICE("price"),
    QUANTITY("quantity");

    private final String name;

    ProductColumn(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
