package com.ekartapplication.ekart.service.user;

import com.ekartapplication.ekart.dao.KartDAO;
import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.dao.WalletDAO;
import com.ekartapplication.ekart.exception.exceptions.EntityNotFoundException;
import com.ekartapplication.ekart.exception.exceptions.InvalidUserDataException;
import com.ekartapplication.ekart.exception.exceptions.UserAlreadyExistsException;
import com.ekartapplication.ekart.exception.repoexception.AppServerException;
import com.ekartapplication.ekart.model.User;
import com.ekartapplication.ekart.repository.kart.KartRepository;
import com.ekartapplication.ekart.repository.user.UserRepository;
import com.ekartapplication.ekart.repository.wallet.WalletRepository;
import com.ekartapplication.ekart.utils.IDGenUtil;
import com.ekartapplication.ekart.utils.mapper.UserModelToDao;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KartRepository kartRepository;
    private final WalletRepository walletRepository;
    private final UserModelToDao userModelToDao;

    public UserServiceImpl(UserRepository userRepository,
                           KartRepository kartRepository,
                           WalletRepository walletRepository,
                           UserModelToDao userModelToDao) {
        this.userRepository = userRepository;
        this.kartRepository = kartRepository;
        this.walletRepository = walletRepository;
        this.userModelToDao = userModelToDao;
    }

    @Override
    @Transactional
    public String createUser(User user) {
        validateCreateUser(user);
        if (isUserNameExists(user.getUserName())) {
            throw new UserAlreadyExistsException("Username is already taken");
        }

        UserDAO userDAO = userModelToDao.mapperModelToDAO(user);
        if (userDAO == null) {
            throw new AppServerException("User mapping failed");
        }
        if (isUserExists(userDAO.getId())) {
            throw new UserAlreadyExistsException("User id already exists");
        }
        if (!userRepository.saveUser(userDAO)) {
            throw new AppServerException("User could not be created");
        }

        WalletDAO wallet = new WalletDAO();
        wallet.setWalletId(IDGenUtil.getUniqueAlphaNumericString(8));
        wallet.setUserId(user.getUserName());
        wallet.setAvlBal(0);
        wallet.setLastTransaction(0);
        wallet.setUsageRewardPoints(0);
        if (!walletRepository.addMoneyToWalletInsert(wallet)) {
            throw new AppServerException("User wallet could not be created");
        }

        KartDAO kart = new KartDAO();
        kart.setKartId(IDGenUtil.getUniqueAlphaNumericString(8));
        kart.setUserId(user.getUserName());
        kart.setProducts("");
        kart.setTotalProducts(0);
        kart.setPayTotal(0);
        kart.setTotalProductsPrice(0);
        kart.setDiscountPrice(0);
        kart.setEarnRewardPoints(0);
        if (!kartRepository.addOneProductToKart(kart)) {
            throw new AppServerException("User cart could not be created");
        }

        return userDAO.getId();
    }

    @Override
    public UserDAO getUser(String userName) {
        requireText(userName, "Username is required");
        return findUserOrThrow(userRepository.getUserById(userName), userName);
    }

    @Override
    @Transactional
    public String updateUser(User user) {
        validateUpdateUser(user);
        UserDAO existingUser = findUserOrThrow(
                userRepository.getUserByUserName(user.getUserName()), user.getUserName());

        UserDAO updatedUser = userModelToDao.mapperModelToDAO(user);
        if (updatedUser == null) {
            throw new AppServerException("User mapping failed");
        }
        updatedUser.setId(existingUser.getId());
        updatedUser.setUserName(existingUser.getUserName());
        updatedUser.setPassword(existingUser.getPassword());
        updatedUser.setRole(existingUser.getRole());
        updatedUser.setCreatedDate(existingUser.getCreatedDate());

        if (!userRepository.updateUser(updatedUser)) {
            throw new AppServerException("User could not be updated");
        }
        return existingUser.getId();
    }

    @Override
    public List<UserDAO> getUsers() {
        List<UserDAO> users = userRepository.getUsers();
        if (users == null) {
            throw new AppServerException("User list could not be retrieved");
        }
        return users;
    }

    @Override
    @Transactional
    public String deleteUser(String userName) {
        requireText(userName, "Username is required");
        UserDAO existingUser = findUserOrThrow(
                userRepository.getUserById(userName), userName);

        try {
            kartRepository.deleteKartByUserId(existingUser.getUserName());
            walletRepository.deleteKartByUserId(existingUser.getUserName());
        } catch (DataAccessException exception) {
            throw new AppServerException("User-related data could not be deleted", exception);
        }

        if (!userRepository.deleteUser(userName)) {
            throw new AppServerException("User could not be deleted");
        }
        return "Deleted...";
    }

    @Override
    public boolean isUserExistsByUserName(String userName) {
        return isUserNameExists(userName);
    }

    @Override
    public UserDAO getUserByUserName(String userName) {
        requireText(userName, "Username is required");
        return findUserOrThrow(userRepository.getUserByUserName(userName), userName);
    }

    public boolean isUserExists(String userName) {
        requireText(userName, "Username is required");
        return userRepository.userCount(userName) > 0;
    }

    public boolean isUserNameExists(String userName) {
        requireText(userName, "Username is required");
        return userRepository.userNameCount(userName) > 0;
    }

    public UserDAO getUserByUserNameAndPassword(String username, String password) {
        requireText(username, "Username is required");
        requireText(password, "Password is required");
        return userRepository.getUserByUDANDPW(username, password);
    }

    private UserDAO findUserOrThrow(UserDAO user, String userName) {
        if (user == null) {
            throw new EntityNotFoundException("User not found: " + userName);
        }
        return user;
    }

    private void validateCreateUser(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User details are required");
        }
        requireText(user.getFirstName(), "First name is required");
        requireText(user.getLastName(), "Last name is required");
        requireText(user.getUserName(), "Username is required");
        requireText(user.getPassword(), "Password is required");
        requireText(user.getEmailId(), "Email is required");
        requireText(user.getMobileNum(), "Mobile number is required");
        requireText(user.getHouseNo(), "House number is required");
        requireText(user.getLandmark(), "Landmark is required");
        requireText(user.getCity(), "City is required");
        requireText(user.getPincode(), "Pincode is required");
        requireText(user.getRole(), "Role is required");
    }

    private void validateUpdateUser(User user) {
        if (user == null) {
            throw new InvalidUserDataException("User details are required");
        }
        requireText(user.getUserName(), "Username is required");
        requireText(user.getFirstName(), "First name is required");
        requireText(user.getLastName(), "Last name is required");
        requireText(user.getEmailId(), "Email is required");
        requireText(user.getMobileNum(), "Mobile number is required");
        requireText(user.getHouseNo(), "House number is required");
        requireText(user.getLandmark(), "Landmark is required");
        requireText(user.getCity(), "City is required");
        requireText(user.getPincode(), "Pincode is required");
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidUserDataException(message);
        }
    }
}
