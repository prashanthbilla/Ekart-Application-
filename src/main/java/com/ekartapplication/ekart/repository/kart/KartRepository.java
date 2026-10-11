package com.ekartapplication.ekart.repository.kart;

import com.ekartapplication.ekart.dao.KartDAO;

public interface KartRepository {
    int isUserExists(String userId);

    KartDAO getKartDetails(String userId);

    boolean addOneProductToKart(KartDAO kartDAO);

    boolean updateOneProductToKart(KartDAO kartDAO);

    void deleteKartByUserId(String userName);
}
