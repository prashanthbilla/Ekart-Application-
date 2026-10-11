package com.ekartapplication.ekart.exception.repoexception;

public class AppServerException extends RuntimeException {
    public AppServerException(String message) {
        super(message);
    }

    public AppServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
