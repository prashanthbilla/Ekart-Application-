package com.ekartapplication.ekart.service.product;

import com.ekartapplication.ekart.utils.enums.ProductFilterMode;
import com.ekartapplication.ekart.model.Product;
import com.ekartapplication.ekart.repository.product.ProductRepository;
import com.ekartapplication.ekart.utils.IDGenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository repository;

    @Override
    public String createProduct(Product product) throws IOException {
        product.setProductId(IDGenUtil.getUniqueAlphaNumericString(8));
        boolean isExists = isProductExists(product.getProductId());
        if (isExists)
            return "Already exists";
        boolean isCreated = repository.createProduct(product);
        if (!isCreated)
            return "Not created";
        return product.getProductId();
    }

    @Override
    public Product getProduct(String productId) {
        Product product = repository.getOneProduct(productId);
        if (Objects.isNull(product))
            return null;
        return product;
    }

    @Override
    public String updateProduct(Product product) {
        boolean isExists = isProductExists(product.getProductId());
        if (!isExists)
            return "Not found";
        boolean isUpdated = repository.updateProduct(product);
        if (!isUpdated)
            return "Not updated";
        return product.getProductId();
    }

    @Override
    public List<Product> getProducts(ProductFilterMode mode, String categoryId) {
        List<Product> products = switch (mode) {
            case NONE -> repository.getProducts();
            case CATEGORY_ID -> repository.getProductsByCategory(categoryId);
        };
        if (Objects.isNull(products))
            return new ArrayList<>();
        return products;
    }

    @Override
    public String deleteProduct(String productId) {
        boolean isExists = isProductExists(productId);
        if (!isExists)
            return "Not found";
        boolean isDeleted = repository.deleteProduct(productId);
        if (!isDeleted)
            return "Not deleted";
        return "Deleted.....";
    }

    public boolean isProductExists(String productId) {
        int count = repository.productCount(productId);
        return count == 1;
    }
}
