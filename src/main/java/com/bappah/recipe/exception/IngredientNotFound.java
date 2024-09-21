package com.bappah.recipe.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IngredientNotFound extends RuntimeException {

    public IngredientNotFound(String message) {
        super(message);
    }

    public IngredientNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
