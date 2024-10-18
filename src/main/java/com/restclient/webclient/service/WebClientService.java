package com.restclient.webclient.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public <T> T getWithParams(String url, Object params, ParameterizedTypeReference<T> clazz){
        String uriString = buildUriWithParams(url, params);
        return webClient.get()
                        .uri(uriString)
                        .retrieve()
                        .bodyToMono(clazz)
                        .block();

    }

    public <T> T getWithParams(String url, Object params, Class<T> clazz){
        String uriString = buildUriWithParams(url, params);
        return webClient.get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(clazz)
                .block();

    }

    public <T> T getWithPathVariable(String url, Object value, Class<T> clazz){
        return webClient.get()
                .uri(url, value)
                .retrieve()
                .bodyToMono(clazz)
                .block();

    }

    public <T> T getWithPathVariable(String url, Object value, ParameterizedTypeReference<T> clazz){
        return webClient.get()
                        .uri(url, value)
                        .retrieve()
                        .bodyToMono(clazz)
                        .block();

    }

    public <T> T postWithBody(String url, Object obj, Class<T> clazz){
        return webClient.post()
                        .uri(url)
                        .bodyValue(obj)
                        .retrieve()
                        .bodyToMono(clazz)
                        .block();

    }

    public <T> T postWithBody(String url, Object obj, ParameterizedTypeReference<T> clazz){
        return webClient.post()
                .uri(url)
                .bodyValue(obj)
                .retrieve()
                .bodyToMono(clazz)
                .block();

    }

    private String buildUriWithParams(String url, Object params) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);
        Map<String, Object> paramMap = objectMapper.convertValue(params, new TypeReference<>() {});
        paramMap.forEach((key, value) -> {
            if (value instanceof List) {
                ((List<?>) value).forEach(item -> urlBuilder.queryParam(key, item));
            } else if (value instanceof Map) {
                ((Map<?, ?>) value).forEach((mapKey, mapValue) ->
                        urlBuilder.queryParam(key + "[" + mapKey + "]", mapValue));
            } else {
                urlBuilder.queryParam(key, value);
            }
        });
        return urlBuilder.build().toString();
    }



}
