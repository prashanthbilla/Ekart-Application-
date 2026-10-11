package com.ekartapplication.ekart.repository.kart;

import com.ekartapplication.ekart.utils.mapper.KartMapperRepo;
import com.ekartapplication.ekart.dao.KartDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KartRepositoryImpl implements KartRepository {

    @Autowired
    JdbcTemplate template;

    @Override
    public int isUserExists(String userId) {
        try {
            return template
                    .queryForObject("select count(user_id) from kart_info where user_id = ? ", new Object[]{userId}, int.class);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public KartDAO getKartDetails(String userId) {
        KartDAO kartDAO;
        try {
            String sql = "SELECT * FROM kart_info WHERE user_id = \"" + userId
                    + "\";";
            kartDAO = template.queryForObject(sql, new KartMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return kartDAO;
    }

    @Override
    public boolean addOneProductToKart(KartDAO kartDAO) {
        try {
            String sql = "INSERT INTO kart_info (kart_id, user_id, products, product_count, total_price, discount_price, pay, get_reward_points) VALUES (" +
                    "\'" + kartDAO.getKartId() +
                    "\', \'" + kartDAO.getUserId() +
                    "\', \'" + kartDAO.getProducts() +
                    "\', \'" + kartDAO.getTotalProducts() +
                    "\', \'" + kartDAO.getTotalProductsPrice() +
                    "\', \'" + kartDAO.getDiscountPrice() +
                    "\', \'" + kartDAO.getPayTotal() +
                    "\', \'" + kartDAO.getEarnRewardPoints() +
                    "\');";
            int response = template.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateOneProductToKart(KartDAO kartDAO) {
        try {
            String sql = "UPDATE kart_info SET products = \'" + kartDAO.getProducts() +
                    "\',product_count = \'" + kartDAO.getTotalProducts() +
                    "\',total_price = \'" + kartDAO.getTotalProductsPrice() +
                    "\',discount_price = \'" + kartDAO.getDiscountPrice() +
                    "\',pay = \'" + kartDAO.getPayTotal() +
                    "\',get_reward_points = \'" + kartDAO.getEarnRewardPoints() +
                    "\' where user_id = \'" + kartDAO.getUserId() + "\';";
            int response = template.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteKartByUserId(String userName) {
        String sql = "DELETE FROM kart_info WHERE user_id = ?";
        template.update(sql, userName);
    }
}
