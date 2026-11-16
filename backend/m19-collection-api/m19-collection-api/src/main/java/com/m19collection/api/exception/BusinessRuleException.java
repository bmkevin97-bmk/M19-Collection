package com.m19collection.api.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(
                message
        );
    }
}