package com.restclient.restclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(ObjectMapper objectMapper, HttpClient httpClient) {
        return RestClient.builder()
                .messageConverters(
                        converters -> {
                            converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                            converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
                        })
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }
}
