package com.ltp.library;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(String message) {

        super(message);
    }
}
