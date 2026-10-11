package com.ekartapplication.ekart.exception.exceptions;

import org.springframework.util.StringUtils;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(Class clazz) {
        super(EntityNotFoundException.generateMessage(clazz.getSimpleName()));
    }

    private static String generateMessage(String entity) {
        return StringUtils.capitalize(entity) +
                " Entity Not Found ...";
    }
}
