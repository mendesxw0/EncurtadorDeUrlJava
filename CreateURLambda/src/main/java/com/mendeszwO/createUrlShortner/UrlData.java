package com.mendeszwO.createUrlShortner;


import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class UrlData {
    private String originalUrl;
    private int expirationTime;


    public UrlData(String originalUrl, long expirationTimeSeconds) {
    }
}
