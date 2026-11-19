package com.m19collection.api.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresIn) {

}
