package com.m19collection.api.service;

public interface TokenIssuer {

    IssuedToken issue(Long userId, String email);

    record IssuedToken(String value, long expiresInSeconds) {
    }
}