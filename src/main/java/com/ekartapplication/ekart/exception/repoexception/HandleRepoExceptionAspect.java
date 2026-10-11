package com.ekartapplication.ekart.exception.repoexception;

import com.ekartapplication.ekart.exception.exceptions.EntityNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class HandleRepoExceptionAspect {

    @AfterThrowing(value = "@annotation(com.ekartapplication.ekart.exception.repoexception.HandleRepoException)", throwing = "exception")
    public void exceptionsHandler(JoinPoint joinPoint, Exception exception) {
        if (exception instanceof DataIntegrityViolationException) {
            throw new AppServerException("Invalid SQL syntax..");
        } else if (exception instanceof DataAccessException | exception instanceof RecoverableDataAccessException) {
            throw new EntityNotFoundException("Invalid SQL syntax..");
        } else if (exception instanceof NullPointerException) {
            throw new AppServerException("VALIDATION_FAILED");
        } else
            throw new AppServerException("Exception ::" + exception.getMessage());
    }
}
