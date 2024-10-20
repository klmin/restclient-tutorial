package com.restclient.restclient.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("test")
@RequiredArgsConstructor
public class TestProperties {
    private final String url;
    private final String headerKey;
    private final String headerValue;

}
