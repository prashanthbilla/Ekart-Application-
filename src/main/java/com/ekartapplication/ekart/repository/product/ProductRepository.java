package com.ekartapplication.ekart.repository.product;

import com.ekartapplication.ekart.model.Product;

import java.util.List;

public interface ProductRepository {
    int productCount(String productId);

    boolean createProduct(Product product);

    List<Product> getProducts();

    List<Product> getProductsByCategory(String categoryId);

    Product getOneProduct(String productId);

    boolean updateProduct(Product product);

    boolean deleteProduct(String productId);
}
