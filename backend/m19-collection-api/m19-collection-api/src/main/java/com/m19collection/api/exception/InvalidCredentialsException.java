package com.m19collection.api.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {

        super(message);
    }
}