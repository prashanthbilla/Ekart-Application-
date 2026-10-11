package com.ekartapplication.ekart.service.product;

import com.ekartapplication.ekart.utils.enums.ProductFilterMode;
import com.ekartapplication.ekart.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    String createProduct(Product product) throws IOException;

    Product getProduct(String productId);

    String updateProduct(Product product);

    List<Product> getProducts(ProductFilterMode mode, String categoryId);

    String deleteProduct(String productId);
}
