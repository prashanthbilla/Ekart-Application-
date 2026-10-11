package com.ekartapplication.ekart.repository.product;

import com.ekartapplication.ekart.utils.mapper.ProductMapperRepo;
import com.ekartapplication.ekart.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public int productCount(String productId) {
        try {
            return jdbcTemplate
                    .queryForObject("select count(product_name) from product_info where product_id = ? ", new Object[]{productId}, int.class);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean createProduct(Product product) {
        try {
            String sql = "INSERT INTO product_info (product_id, product_name, category_id,  product_image_url, product_details, product_actual_price, " +
                    "offer_percentage, usage_reward_points) VALUES (" +
                    "\'" + product.getProductId() + "\', \'" + product.getProductName() + "\', \'" + product.getCategoryId() +
                    "\', \'" + product.getProductImageUrl() +
                    "\', \'" + product.getProductDetails() +
                    "\', \'" + product.getActualPrice() +
                    "\', \'" + product.getOfferPercentage() +
                    "\', \'" + product.getUsageRewardPoints() +
                    "\');";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products;
        try {
            String sql = "select * from product_info";
            products = jdbcTemplate.query(sql, new ProductMapperRepo());
            return products;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Product> getProductsByCategory(String categoryId) {
        List<Product> product;
        try {
            String sql = "SELECT * FROM product_info WHERE category_id = \"" + categoryId + "\";";
            product = jdbcTemplate.query(sql, new ProductMapperRepo());
            return product;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Product getOneProduct(String productId) {
        Product product;
        try {
            String sql = "SELECT * FROM product_info WHERE product_id = \"" + productId
                    + "\";";
            product = jdbcTemplate.queryForObject(sql, new ProductMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return product;
    }

    @Override
    public boolean updateProduct(Product product) {
        try {
            String sql = "UPDATE product_info SET product_name = \'"
                    + product.getProductName() + "\',category_id = \'" + product.getCategoryId() + "\',product_image_url = \'" + product.getProductImageUrl() +
                    "\',product_details = \'" + product.getProductDetails() +
                    "\',product_actual_price = \'" + product.getActualPrice() +
                    "\',offer_percentage = \'" + product.getOfferPercentage() +
                    "\',usage_reward_points = \'" + product.getUsageRewardPoints() +
                    "\' where product_id = \'" + product.getProductId() + "\';";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteProduct(String productId) {
        try {
            String sql = "DELETE from product_info where product_id = \"" + productId + "\"";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }
}
