package com.ekartapplication.ekart.repository.user;

import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.exception.repoexception.AppServerException;
import com.ekartapplication.ekart.exception.repoexception.HandleRepoExceptionAspect;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRepositoryExceptionHandlingTest {

    @Test
    void wrapsDatabaseFailureFromAnnotatedRepositoryMethod() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.update(anyString(), any(Object[].class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        UserRepository repository = repositoryProxy(jdbcTemplate);

        AppServerException exception = assertThrows(AppServerException.class,
                () -> repository.saveUser(new UserDAO()));

        assertEquals("Repository database operation failed", exception.getMessage());
        assertInstanceOf(DataIntegrityViolationException.class, exception.getCause());
    }

    private UserRepository repositoryProxy(JdbcTemplate jdbcTemplate) {
        AspectJProxyFactory proxyFactory =
                new AspectJProxyFactory(new UserRepositoryImpl(jdbcTemplate));
        proxyFactory.addAspect(new HandleRepoExceptionAspect());
        return proxyFactory.getProxy();
    }
}
