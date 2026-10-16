package com.example.demo.dto;

public record ShortenUrlResponse(
        String originalUrl,
        String shortCode,
        String shortUrl
) {
}
