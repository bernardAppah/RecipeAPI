package com.bappah.recipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecipeNotFound extends RuntimeException {

    public RecipeNotFound(String message) {
        super(message);
    }

    public RecipeNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
