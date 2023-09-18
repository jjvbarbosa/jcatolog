package com.jvbarbosa.jcatalog.tests;

import com.jvbarbosa.jcatalog.dto.ProductDTO;
import com.jvbarbosa.jcatalog.entities.Category;
import com.jvbarbosa.jcatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(1L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
