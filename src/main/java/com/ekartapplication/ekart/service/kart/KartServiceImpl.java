package com.ekartapplication.ekart.service.kart;

import com.ekartapplication.ekart.dao.KartDAO;
import com.ekartapplication.ekart.model.*;
import com.ekartapplication.ekart.repository.kart.KartRepository;
import com.ekartapplication.ekart.service.product.ProductService;
import com.ekartapplication.ekart.service.user.UserService;
import com.ekartapplication.ekart.utils.IDGenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KartServiceImpl implements KartService {
    @Autowired
    KartRepository repository;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;

    @Override
    public String addMultipleProducts(AddMultipleProductsRequest addProducts) {
        Product product;
        KartDAO kartDAO;
        boolean inserted;
        double discountPrice;
        double payTotal;
        int earnRewardPoints;
        int productsAddedCount = 0;
        StringBuilder notFoundProductIDs = new StringBuilder();
        boolean isUserExistsInKart = isUserExists(addProducts.getUserId());
        boolean isUserExists = userService.isUserExistsByUserName(addProducts.getUserId());
        if (!isUserExists) return "User not found...";
        kartDAO = repository.getKartDetails(addProducts.getUserId());
        if (!kartItemsMoreThanTen(addProducts.getUserId()) && addProducts.getProducts().size() < 10) {
            if (!isUserExistsInKart && kartDAO == null) {
                kartDAO = new KartDAO();
                kartDAO.setKartId(IDGenUtil.getUniqueAlphaNumericString(8));
                kartDAO.setUserId(addProducts.getUserId());
                List<String> productIdsInput = addProducts.getProducts();
                for (String productId : productIdsInput) {
                    product = this.getProduct(productId);
                    if (Objects.isNull(product)) {
                        notFoundProductIDs.append(",").append(productId);
                    } else {
                        if (addProducts.getProducts().size() == 1) {
                            kartDAO.setProducts(productId);
                            kartDAO.setTotalProducts(1);
                            kartDAO.setTotalProductsPrice(product.getActualPrice());
                            discountPrice = (product.getOfferPercentage() * product.getActualPrice()) / 100;
                            kartDAO.setDiscountPrice(discountPrice);
                            payTotal = product.getActualPrice() - discountPrice;
                            kartDAO.setPayTotal(payTotal);
                            kartDAO.setEarnRewardPoints(product.getUsageRewardPoints());
                        } else {
                            kartDAO.setProducts(kartDAO.getProducts() + "," + productId);
                            kartDAO.setTotalProducts(kartDAO.getTotalProducts() + 1);
                            kartDAO.setTotalProductsPrice(kartDAO.getTotalProductsPrice() + product.getActualPrice());
                            discountPrice = (product.getOfferPercentage() * product.getActualPrice()) / 100;
                            kartDAO.setDiscountPrice(kartDAO.getDiscountPrice() + discountPrice);
                            payTotal = kartDAO.getTotalProductsPrice() - kartDAO.getDiscountPrice();
                            kartDAO.setPayTotal(payTotal);
                            earnRewardPoints = kartDAO.getEarnRewardPoints() + product.getUsageRewardPoints();
                            kartDAO.setEarnRewardPoints(earnRewardPoints);
                        }
                        productsAddedCount = productsAddedCount + 1;
                    }
                }
                inserted = repository.addOneProductToKart(kartDAO);
            } else {
                List<String> productIdsInput = addProducts.getProducts();
                for (String productId : productIdsInput) {
                    product = this.getProduct(productId);
                    if (Objects.isNull(product)) {
                        if (notFoundProductIDs.isEmpty()) {
                            notFoundProductIDs.append(productId);
                        } else {
                            notFoundProductIDs.append(",").append(productId);
                        }
                    } else {
                        if (kartDAO.getTotalProducts() == 0) {
                            kartDAO.setProducts(productId);
                            kartDAO.setTotalProducts(1);
                            kartDAO.setTotalProductsPrice(product.getActualPrice());
                            discountPrice = (product.getOfferPercentage() * product.getActualPrice()) / 100;
                            kartDAO.setDiscountPrice(discountPrice);
                            payTotal = product.getActualPrice() - discountPrice;
                            kartDAO.setPayTotal(payTotal);
                            kartDAO.setEarnRewardPoints(product.getUsageRewardPoints());
                        } else {
                            kartDAO.setProducts(kartDAO.getProducts() + "," + productId);
                            kartDAO.setTotalProducts(kartDAO.getTotalProducts() + 1);
                            kartDAO.setTotalProductsPrice(kartDAO.getTotalProductsPrice() + product.getActualPrice());
                            discountPrice = (product.getOfferPercentage() * product.getActualPrice()) / 100;
                            kartDAO.setDiscountPrice(kartDAO.getDiscountPrice() + discountPrice);
                            payTotal = kartDAO.getTotalProductsPrice() - kartDAO.getDiscountPrice();
                            kartDAO.setPayTotal(payTotal);
                            earnRewardPoints = kartDAO.getEarnRewardPoints() + product.getUsageRewardPoints();
                            kartDAO.setEarnRewardPoints(earnRewardPoints);
                        }
                    }
                    productsAddedCount = productsAddedCount + 1;
                }
                inserted = repository.updateOneProductToKart(kartDAO);
            }
            if (!inserted) return "Products not Added";
            if (notFoundProductIDs.isEmpty()) {
                notFoundProductIDs = new StringBuilder("0");
                return "Products Added successfully, TotalProducts in Kart = " + kartDAO.getTotalProducts() + " Total price = " + kartDAO.getTotalProductsPrice() + " Total Added products this time = " + productsAddedCount + " NOTE: some products are not added because products not found = " + notFoundProductIDs;
            } else {
                return "some products added successfully, TotalProducts in Kart = " + kartDAO.getTotalProducts() + " Total price = " + kartDAO.getTotalProductsPrice() + " Total Added products this time = " + productsAddedCount + " NOTE: some products are not added because products not found = " + notFoundProductIDs;
            }
        }
        return "Kart is full (Kart has more than 10 items), Please remove OR buy some items";

    }

    @Override
    public String deleteProductFromKart(String userId, String productId) {
        StringBuilder productIDNew = new StringBuilder();
        KartDAO kartDAO = this.getKartDetailsByUserIdAndProductId(userId);
        Product productInfo = this.getProduct(productId);
        if (!kartDAO.getProducts().isEmpty()) {
            List<String> productIDs = new ArrayList<>(List.of(kartDAO.getProducts().split(",")));
            productIDs.remove(productId);
            if (!productIDs.isEmpty()) {
                for (String product : productIDs) {
                    if (productIDNew.isEmpty()) {
                        productIDNew.append(product);
                    } else {
                        productIDNew.append(",").append(product);
                    }
                }
            }
            kartDAO.setTotalProductsPrice(kartDAO.getTotalProductsPrice() - productInfo.getActualPrice());
            kartDAO.setDiscountPrice((productInfo.getOfferPercentage() * productInfo.getActualPrice()) / 100);
            kartDAO.setPayTotal(kartDAO.getTotalProductsPrice() - kartDAO.getDiscountPrice());
            kartDAO.setEarnRewardPoints(kartDAO.getEarnRewardPoints() - productInfo.getUsageRewardPoints());
            kartDAO.setTotalProducts(kartDAO.getTotalProducts() - 1);
            kartDAO.setProducts(productIDNew.toString());
            repository.updateOneProductToKart(kartDAO);

            return "Removed item from kart";
        }

        return "Item not removed from kart";
    }

    public KartDAO getKartDetailsByUserIdAndProductId(String userId) {
        return repository.getKartDetails(userId);
    }

    @Override
    public KartDetailsResponse getKartDetails(String userId) {
        KartDAO kartDAO = repository.getKartDetails(userId);
        KartDetailsResponse kart = new KartDetailsResponse();
        kart.setKartId(kartDAO.getKartId());
        kart.setUserId(userId);
        kart.setPay(kartDAO.getPayTotal());
        kart.setProductCount(kartDAO.getTotalProducts());
        kart.setTotalPrice(kartDAO.getTotalProductsPrice());
        kart.setDiscountPrice(kartDAO.getDiscountPrice());
        kart.setGetRewardPoints(kartDAO.getEarnRewardPoints());
        String[] products = kartDAO.getProducts().split(",");
        Map<String, Integer> productsmap = new HashMap<>();
        Arrays.stream(products).collect(Collectors.groupingBy(s -> s)).forEach((k, v) -> productsmap.put(k, v.size()));
        kart.setProducts(productsmap);
        return kart;
    }

    public Product getProduct(String productId) {
        return productService.getProduct(productId);
    }

    public boolean isUserExists(String userId) {
        int count = repository.isUserExists(userId);
        return count == 1;
    }

    public boolean kartItemsMoreThanTen(String userId) {
        KartDAO kartDAO = repository.getKartDetails(userId);
        if (Objects.isNull(kartDAO))
            return false;
        String[] products = kartDAO.getProducts().split(",");
        return products.length == 10;
    }

}
