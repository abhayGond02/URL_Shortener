package com.example.demo.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.demo.dto.ShortenUrlRequest;
import com.example.demo.dto.ShortenUrlResponse;
import com.example.demo.entity.UrlMapping;
import com.example.demo.service.UrlShortenerService;

import jakarta.validation.Valid;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {

        this.service = service;

    }

    @PostMapping("/api/urls")
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenUrlResponse shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {

        UrlMapping mapping = service.shortenUrl(request.originalUrl());

        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{code}")
                .buildAndExpand(mapping.getShortCode())
                .toUriString();

        return new ShortenUrlResponse(
                mapping.getOriginalUrl(),
                mapping.getShortCode(),
                shortUrl);

    }

    @GetMapping("/{code:[a-zA-Z0-9]+}")
    public ResponseEntity<Void> redirectToOriginalUrl(
            @PathVariable("code") String shortCode) {

        String originalUrl = service.getOriginalUrl(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

}
