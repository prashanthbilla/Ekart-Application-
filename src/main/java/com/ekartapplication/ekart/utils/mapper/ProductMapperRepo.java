package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.model.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapperRepo implements RowMapper<Product> {

    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getString("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setCategoryId(rs.getString("category_id"));
        product.setProductDetails(rs.getString("product_details"));
        product.setProductImageUrl(rs.getString("product_image_url"));
        product.setActualPrice(rs.getDouble("product_actual_price"));
        product.setOfferPercentage(rs.getInt("offer_percentage"));
        product.setUsageRewardPoints(rs.getInt("usage_reward_points"));
        return product;
    }
}
