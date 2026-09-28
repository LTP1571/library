package com.ltp.library;

public class AuthorHasBooksException extends RuntimeException {
    public AuthorHasBooksException(String message) {
        super(message);
    }
}
