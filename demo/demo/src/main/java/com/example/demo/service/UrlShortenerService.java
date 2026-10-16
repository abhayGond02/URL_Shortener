package com.example.demo.service;

import java.security.SecureRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.UrlMapping;
import com.example.demo.repository.UrlMappingRepository;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository mappingRepository;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();

    public UrlShortenerService(UrlMappingRepository mappingRepository) {

        this.mappingRepository = mappingRepository;

    }

    public UrlMapping shortenUrl(String originalUrl) {

        String shortCode = generateUniqueCode();

        UrlMapping mapping = new UrlMapping(originalUrl, shortCode);

        return mappingRepository.save(mapping);

    }

    private String generateUniqueCode() {

        String shortCode;

        do {

            shortCode = generateRandomCode();

        } while (mappingRepository.findByShortCode(shortCode)
                .isPresent());

        return shortCode;

    }

    public String getOriginalUrl(String shortCode) {

        UrlMapping mapping = mappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Short URL not found"));

        return mapping.getOriginalUrl();
    }

    private String generateRandomCode() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {

            int index = secureRandom.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));

        }

        return code.toString();

    }

}