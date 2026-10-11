package com.ekartapplication.ekart.service.user;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.exception.exceptions.EntityNotFoundException;
import com.ekartapplication.ekart.exception.exceptions.InvalidUserDataException;
import com.ekartapplication.ekart.exception.exceptions.UserAlreadyExistsException;
import com.ekartapplication.ekart.exception.repoexception.AppServerException;
import com.ekartapplication.ekart.model.User;
import com.ekartapplication.ekart.repository.kart.KartRepository;
import com.ekartapplication.ekart.repository.user.UserRepository;
import com.ekartapplication.ekart.repository.wallet.WalletRepository;
import com.ekartapplication.ekart.utils.mapper.UserModelToDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.sql.Date;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String USERNAME = "jane-test";

    @Mock
    private UserRepository userRepository;

    @Mock
    private KartRepository kartRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserModelToDao userModelToDao;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        user = validUser();
        userDAO = validUserDAO();
    }

    @Test
    void createUserRejectsNullOrIncompleteInput() {
        assertThrows(InvalidUserDataException.class, () -> userService.createUser(null));

        user.setUserName(" ");
        assertThrows(InvalidUserDataException.class, () -> userService.createUser(user));
        verify(userRepository, never()).userNameCount(any());
    }

    @Test
    void createUserRejectsEveryMissingRequiredField() {
        assertInvalidCreateField(value -> value.setFirstName(" "));
        assertInvalidCreateField(value -> value.setLastName(null));
        assertInvalidCreateField(value -> value.setUserName(""));
        assertInvalidCreateField(value -> value.setPassword(null));
        assertInvalidCreateField(value -> value.setEmailId(" "));
        assertInvalidCreateField(value -> value.setMobileNum(null));
        assertInvalidCreateField(value -> value.setHouseNo(""));
        assertInvalidCreateField(value -> value.setLandmark(" "));
        assertInvalidCreateField(value -> value.setCity(null));
        assertInvalidCreateField(value -> value.setPincode(""));
        assertInvalidCreateField(value -> value.setRole(" "));
        verify(userRepository, never()).userNameCount(any());
    }

    @Test
    void createUserRejectsDuplicateUsername() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(1);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void createUserRejectsDuplicateMappedId() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(0);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.userCount(userDAO.getId())).thenReturn(1);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void createUserInitializesDependenciesAndReturnsGeneratedId() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(0);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.saveUser(userDAO)).thenReturn(true);
        when(walletRepository.addMoneyToWalletInsert(any())).thenReturn(true);
        when(kartRepository.addOneProductToKart(any())).thenReturn(true);

        assertEquals(userDAO.getId(), userService.createUser(user));
    }

    @Test
    void createUserFailsIfTheUserCannotBeSaved() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(0);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.saveUser(userDAO)).thenReturn(false);

        assertThrows(AppServerException.class, () -> userService.createUser(user));
        verify(walletRepository, never()).addMoneyToWalletInsert(any());
        verify(kartRepository, never()).addOneProductToKart(any());
    }

    @Test
    void createUserReportsMappingFailure() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(0);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(null);

        assertThrows(AppServerException.class, () -> userService.createUser(user));
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void createUserFailsIfWalletCannotBeInitialized() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(0);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.saveUser(userDAO)).thenReturn(true);
        when(walletRepository.addMoneyToWalletInsert(any())).thenReturn(false);

        assertThrows(AppServerException.class, () -> userService.createUser(user));
        verify(kartRepository, never()).addOneProductToKart(any());
    }

    @Test
    void createUserFailsIfCartCannotBeInitialized() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(0);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.saveUser(userDAO)).thenReturn(true);
        when(walletRepository.addMoneyToWalletInsert(any())).thenReturn(true);
        when(kartRepository.addOneProductToKart(any())).thenReturn(false);

        assertThrows(AppServerException.class, () -> userService.createUser(user));
    }

    @Test
    void getUserThrowsNotFoundForUnknownUsername() {
        when(userRepository.getUserById(USERNAME)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(USERNAME));
    }

    @Test
    void getUserReturnsExistingUser() {
        when(userRepository.getUserById(USERNAME)).thenReturn(userDAO);

        assertEquals(userDAO, userService.getUser(USERNAME));
    }

    @Test
    void getUsersRejectsUnexpectedNullRepositoryResult() {
        when(userRepository.getUsers()).thenReturn(null);

        assertThrows(AppServerException.class, () -> userService.getUsers());
    }

    @Test
    void getUsersReturnsRepositoryResults() {
        when(userRepository.getUsers()).thenReturn(Collections.singletonList(userDAO));

        assertEquals(1, userService.getUsers().size());
    }

    @Test
    void updateUserRejectsMissingUserAndPreservesStoredIdentityAndCredentials() {
        when(userRepository.getUserByUserName(USERNAME)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(user));

        UserDAO existing = validUserDAO();
        existing.setId("persisted-id");
        existing.setPassword("stored-hash");
        existing.setRole("ROLE_USER");
        existing.setCreatedDate(Date.valueOf("2024-01-01"));
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.getUserByUserName(USERNAME)).thenReturn(existing);
        when(userRepository.updateUser(any(UserDAO.class))).thenReturn(true);

        assertEquals("persisted-id", userService.updateUser(user));
        org.mockito.ArgumentCaptor<UserDAO> updatedUser =
                org.mockito.ArgumentCaptor.forClass(UserDAO.class);
        verify(userRepository).updateUser(updatedUser.capture());
        assertEquals("persisted-id", updatedUser.getValue().getId());
        assertEquals("stored-hash", updatedUser.getValue().getPassword());
        assertEquals("ROLE_USER", updatedUser.getValue().getRole());
        assertEquals(existing.getCreatedDate(), updatedUser.getValue().getCreatedDate());
    }

    @Test
    void updateUserReportsPersistenceFailure() {
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(userDAO);
        when(userRepository.getUserByUserName(USERNAME)).thenReturn(userDAO);
        when(userRepository.updateUser(any(UserDAO.class))).thenReturn(false);

        assertThrows(AppServerException.class, () -> userService.updateUser(user));
    }

    @Test
    void updateUserReportsMappingFailure() {
        when(userRepository.getUserByUserName(USERNAME)).thenReturn(userDAO);
        when(userModelToDao.mapperModelToDAO(user)).thenReturn(null);

        assertThrows(AppServerException.class, () -> userService.updateUser(user));
        verify(userRepository, never()).updateUser(any());
    }

    @Test
    void updateUserRejectsNullAndEveryMissingRequiredField() {
        assertThrows(InvalidUserDataException.class, () -> userService.updateUser(null));
        assertInvalidUpdateField(value -> value.setUserName(" "));
        assertInvalidUpdateField(value -> value.setFirstName(null));
        assertInvalidUpdateField(value -> value.setLastName(""));
        assertInvalidUpdateField(value -> value.setEmailId(" "));
        assertInvalidUpdateField(value -> value.setMobileNum(null));
        assertInvalidUpdateField(value -> value.setHouseNo(""));
        assertInvalidUpdateField(value -> value.setLandmark(" "));
        assertInvalidUpdateField(value -> value.setCity(null));
        assertInvalidUpdateField(value -> value.setPincode(""));
        verify(userRepository, never()).getUserByUserName(any());
    }

    @Test
    void deleteUserRejectsMissingUserAndWrapsRelatedDataFailure() {
        when(userRepository.getUserById(USERNAME)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(USERNAME));

        when(userRepository.getUserById(USERNAME)).thenReturn(userDAO);
        org.mockito.Mockito.doThrow(new DataAccessResourceFailureException("database unavailable"))
                .when(kartRepository).deleteKartByUserId(USERNAME);

        assertThrows(AppServerException.class, () -> userService.deleteUser(USERNAME));
        verify(userRepository, never()).deleteUser(USERNAME);
    }

    @Test
    void deleteUserWrapsWalletDeletionFailure() {
        when(userRepository.getUserById(USERNAME)).thenReturn(userDAO);
        org.mockito.Mockito.doThrow(new DataAccessResourceFailureException("database unavailable"))
                .when(walletRepository).deleteKartByUserId(USERNAME);

        assertThrows(AppServerException.class, () -> userService.deleteUser(USERNAME));
        verify(userRepository, never()).deleteUser(USERNAME);
    }

    @Test
    void deleteUserReportsFailureToDeleteAccount() {
        when(userRepository.getUserById(USERNAME)).thenReturn(userDAO);
        when(userRepository.deleteUser(USERNAME)).thenReturn(false);

        assertThrows(AppServerException.class, () -> userService.deleteUser(USERNAME));
        verify(kartRepository).deleteKartByUserId(USERNAME);
        verify(walletRepository).deleteKartByUserId(USERNAME);
    }

    @Test
    void deleteUserRemovesRelatedDataAndAccount() {
        when(userRepository.getUserById(USERNAME)).thenReturn(userDAO);
        when(userRepository.deleteUser(USERNAME)).thenReturn(true);

        assertEquals("Deleted...", userService.deleteUser(USERNAME));
        verify(kartRepository).deleteKartByUserId(USERNAME);
        verify(walletRepository).deleteKartByUserId(USERNAME);
    }

    @Test
    void existenceChecksTreatAnyPositiveCountAsExisting() {
        when(userRepository.userNameCount(USERNAME)).thenReturn(2);
        when(userRepository.userCount(USERNAME)).thenReturn(2);

        assertTrue(userService.isUserExistsByUserName(USERNAME));
        assertTrue(userService.isUserExists(USERNAME));
        assertFalse(userService.isUserExistsByUserName("another-user"));
    }

    @Test
    void existenceCheckReturnsFalseWhenUserDoesNotExist() {
        when(userRepository.userCount(USERNAME)).thenReturn(0);

        assertFalse(userService.isUserExists(USERNAME));
    }

    @Test
    void getUserByUsernameReturnsUserOrThrowsNotFound() {
        when(userRepository.getUserByUserName(USERNAME)).thenReturn(userDAO);
        assertEquals(userDAO, userService.getUserByUserName(USERNAME));

        when(userRepository.getUserByUserName(USERNAME)).thenReturn(null);
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByUserName(USERNAME));
    }

    @Test
    void getUserByUsernameAndPasswordValidatesAndDelegates() {
        when(userRepository.getUserByUDANDPW(USERNAME, "encoded-password")).thenReturn(userDAO);

        assertEquals(userDAO,
                userService.getUserByUserNameAndPassword(USERNAME, "encoded-password"));
        assertThrows(InvalidUserDataException.class,
                () -> userService.getUserByUserNameAndPassword(" ", "encoded-password"));
        assertThrows(InvalidUserDataException.class,
                () -> userService.getUserByUserNameAndPassword(USERNAME, null));
    }

    @Test
    void getUserByUsernameAndPasswordReturnsNullWhenRepositoryDoesNotFindMatch() {
        when(userRepository.getUserByUDANDPW(USERNAME, "wrong-password")).thenReturn(null);

        assertNull(userService.getUserByUserNameAndPassword(USERNAME, "wrong-password"));
    }

    @Test
    void methodsRejectBlankUsernames() {
        assertThrows(InvalidUserDataException.class, () -> userService.getUser(" "));
        assertThrows(InvalidUserDataException.class, () -> userService.getUserByUserName(null));
        assertThrows(InvalidUserDataException.class, () -> userService.isUserExistsByUserName(""));
    }

    private User validUser() {
        User value = new User();
        value.setFirstName("Jane");
        value.setLastName("Tester");
        value.setUserName(USERNAME);
        value.setPassword("encoded-test-password");
        value.setEmailId("jane@example.invalid");
        value.setMobileNum("5550100");
        value.setHouseNo("10");
        value.setLandmark("Test Lane");
        value.setCity("Test City");
        value.setPincode("12345");
        value.setRole("ROLE_USER");
        return value;
    }

    private void assertInvalidCreateField(java.util.function.Consumer<User> mutation) {
        User invalidUser = validUser();
        mutation.accept(invalidUser);
        assertThrows(InvalidUserDataException.class, () -> userService.createUser(invalidUser));
    }

    private void assertInvalidUpdateField(java.util.function.Consumer<User> mutation) {
        User invalidUser = validUser();
        mutation.accept(invalidUser);
        assertThrows(InvalidUserDataException.class, () -> userService.updateUser(invalidUser));
    }

    private UserDAO validUserDAO() {
        UserDAO value = new UserDAO();
        value.setId("generated-id");
        value.setFirstName("Jane");
        value.setLastName("Tester");
        value.setFullName("Jane Tester");
        value.setUserName(USERNAME);
        value.setPassword("encoded-test-password");
        value.setEmailId("jane@example.invalid");
        value.setMobileNum("5550100");
        value.setHouseNo("10");
        value.setLandmark("Test Lane");
        value.setCity("Test City");
        value.setPincode("12345");
        value.setRole("ROLE_USER");
        value.setCreatedDate(Date.valueOf("2024-01-01"));
        value.setUpdatedDate(Date.valueOf("2024-01-02"));
        return value;
    }
}
