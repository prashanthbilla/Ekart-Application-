package com.ekartapplication.ekart.service.user;

import com.ekartapplication.ekart.dao.KartDAO;
import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.dao.WalletDAO;
import com.ekartapplication.ekart.repository.kart.KartRepository;
import com.ekartapplication.ekart.repository.wallet.WalletRepository;
import com.ekartapplication.ekart.utils.IDGenUtil;
import com.ekartapplication.ekart.utils.mapper.UserModelToDao;
import com.ekartapplication.ekart.model.User;
import com.ekartapplication.ekart.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    KartRepository kartRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    UserModelToDao userModelToDao;

    @Override
    public String createUser(User user) {
        UserDAO userDAO = userModelToDao.mapperModelToDAO(user);
        boolean isExists = isUserExists(userDAO.getId());
        boolean isUserNameExists = isUserNameExists(userDAO.getUserName());
        if (!isUserNameExists) {
            if (isExists)
                return "Already exists";
            boolean isCreated = userRepository.saveUser(userDAO);
            if (!isCreated)
                return "Not created";
            //After this we have to allot user to kart
            WalletDAO walletDAO = new WalletDAO();
            walletDAO.setWalletId(IDGenUtil.getUniqueAlphaNumericString(8));
            walletDAO.setUserId(user.getUserName());
            walletDAO.setAvlBal(0);
            walletDAO.setLastTransaction(0);
            walletDAO.setUsageRewardPoints(0);
            walletRepository.addMoneyToWalletInsert(walletDAO);

            //After this we have to allot user to wallet
            KartDAO kartDAO = new KartDAO();
            kartDAO.setKartId(IDGenUtil.getUniqueAlphaNumericString(8));
            kartDAO.setUserId(user.getUserName());
            kartDAO.setProducts("");
            kartDAO.setTotalProducts(0);
            kartDAO.setPayTotal(0);
            kartDAO.setTotalProductsPrice(0);
            kartDAO.setDiscountPrice(0);
            kartDAO.setEarnRewardPoints(0);
            kartRepository.addOneProductToKart(kartDAO);

            return userDAO.getId();
        }
        return "UserName taken, try another one";
    }

    @Override
    public UserDAO getUser(String userId) {
        UserDAO user = userRepository.getUserById(userId);
        if (Objects.isNull(user))
            return null;
        return user;
    }

    @Override
    public String updateUser(User user) {
        boolean isExists = isUserExists(user.getUserName());
        if (!isExists)
            return "Not found";
        UserDAO alreadyUser = userRepository.getUserById(user.getUserName());
        UserDAO userDAO = userModelToDao.mapperModelToDAO(user);
        userDAO.setUserName(alreadyUser.getUserName());
        userDAO.setPassword(alreadyUser.getPassword());
        userDAO.setRole(alreadyUser.getRole());
        userDAO.setCreatedDate(alreadyUser.getCreatedDate());
        boolean isUpdated = userRepository.updateUser(userDAO);
        if (!isUpdated)
            return "Not updated";
        return user.getId();
    }

    @Override
    public List<UserDAO> getUsers() {
        return userRepository.getUsers();
    }

    public String deleteUser(String userId) {

        boolean isExists = isUserExists(userId);
        if (!isExists)
            return "Not found";
        UserDAO userDAO = userRepository.getUserById(userId);
        boolean isDeleted = userRepository.deleteUser(userId);
        if (!isDeleted)
            return "Not deleted";
        // Delete kart details for this user
        kartRepository.deleteKartByUserId(userDAO.getUserName());
        // Delete wallet details for this user
        walletRepository.deleteKartByUserId(userDAO.getUserName());
        return "Deleted...";
    }

    @Override
    public boolean isUserExistsByUserName(String userName) {
        return isUserNameExists(userName);
    }

    @Override
    public UserDAO getUserByUserName(String userName) {
        UserDAO user = userRepository.getUserByUserName(userName);
        if (Objects.isNull(user))
            return null;
        return user;
    }

    public boolean isUserExists(String userId) {
        int count = userRepository.userCount(userId);
        return count == 1;
    }

    public boolean isUserNameExists(String userName) {
        int count = userRepository.userNameCount(userName);
        return count == 1;
    }

    public UserDAO getUserByUserNameAndPassword(String username, String password) {
        return userRepository.getUserByUDANDPW(username, password);
    }

}
