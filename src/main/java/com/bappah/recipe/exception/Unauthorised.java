package com.bappah.recipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class Unauthorised extends RuntimeException {

    public Unauthorised(String message) {
        super(message);
    }
}
