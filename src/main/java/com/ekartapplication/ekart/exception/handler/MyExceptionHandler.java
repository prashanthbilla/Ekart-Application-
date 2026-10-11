package com.ekartapplication.ekart.exception.handler;

import com.ekartapplication.ekart.exception.errorresponse.ApiErrorResponse;
import com.ekartapplication.ekart.exception.exceptions.InvalidUserDataException;
import com.ekartapplication.ekart.exception.exceptions.EntityNotFoundException;
import com.ekartapplication.ekart.exception.exceptions.UserAlreadyExistsException;
import com.ekartapplication.ekart.exception.repoexception.AppServerException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class MyExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    private ResponseEntity<Object> buildResponseEntity(ApiErrorResponse apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotCreated(
            EntityNotFoundException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    protected ResponseEntity<Object> handleInvalidUserData(InvalidUserDataException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    protected ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.CONFLICT);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(AppServerException.class)
    protected ResponseEntity<Object> handleAppServerException(AppServerException ex) {
        logger.error("Repository operation failed", ex);
        ApiErrorResponse apiError = new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("An internal error occurred");
        return buildResponseEntity(apiError);
    }
}
