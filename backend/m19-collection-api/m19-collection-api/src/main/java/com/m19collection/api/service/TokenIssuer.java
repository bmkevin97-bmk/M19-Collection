package com.m19collection.api.service;

public interface TokenIssuer {

    IssuedToken issue(Long userId, String email, String role);

    record IssuedToken(String value, long expiresInSeconds) {
    }
}