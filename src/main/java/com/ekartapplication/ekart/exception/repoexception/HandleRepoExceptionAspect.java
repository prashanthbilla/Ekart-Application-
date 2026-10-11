package com.ekartapplication.ekart.exception.repoexception;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HandleRepoExceptionAspect {

    @Around("@annotation(com.ekartapplication.ekart.exception.repoexception.HandleRepoException)")
    public Object handleRepositoryException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (DataAccessException exception) {
            throw new AppServerException("Repository database operation failed", exception);
        } catch (AppServerException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new AppServerException("Repository operation failed", exception);
        }
    }
}
