package com.mendeszwO.RedirectURLShortener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrlData {
    private String originalUrl;
    private long expirationTime;

}
