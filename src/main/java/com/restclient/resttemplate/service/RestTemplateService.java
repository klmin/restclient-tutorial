package com.restclient.resttemplate.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restclient.resttemplate.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RestTemplateService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public <T, Q, B> T exchange(String url, HttpMethod method, HttpHeaders headers, Q queryString, B body,
                                ParameterizedTypeReference<T> responseType) {
        RequestDTO<Q, B> requestDTO = this.requestDTO(queryString, body, headers);
        return this.exchangeInternal(url, method, requestDTO, responseType, null);
    }

    public <T, Q, B> T exchange(String url, HttpMethod method, HttpHeaders headers, Q queryString, B body, Class<T> clazz) {
        RequestDTO<Q, B> requestDTO = this.requestDTO(queryString, body, headers);
        return this.exchangeInternal(url, method, requestDTO, null, clazz);
    }

    private <T, Q, B> T exchangeInternal(String url, HttpMethod method, RequestDTO<Q, B> requestDTO,
                                         ParameterizedTypeReference<T> responseType, Class<T> clazz) {
        try {

            ResponseEntity<T> responseEntity = (clazz != null)
                    ? this.callExchange(url, method, requestDTO, clazz)
                    : this.callExchangeGeneric(url, method, requestDTO, responseType);

            logStatusCode(responseEntity);

            return responseEntity.getBody();

        } catch (Exception e) {
            handleExchangeException(e);
            return null;
        }
    }

    private <T> ResponseEntity<T> callExchangeGeneric(String url, HttpMethod method, RequestDTO<?, ?> requestDTO,
                                                      ParameterizedTypeReference<T> response)  {
        return restTemplate.exchange(this.setURI(url, requestDTO), method, this.setHttpEntity(requestDTO), response);
    }

    private <T, Q, B> ResponseEntity<T> callExchange(String url, HttpMethod method, RequestDTO<Q, B> requestDTO,
                                      Class<T> clazz) {

        return restTemplate.exchange(this.setURI(url, requestDTO), method, this.setHttpEntity(requestDTO), clazz);

    }

    private <Q, B> RequestDTO<Q, B> requestDTO(Q queryString, B body, HttpHeaders header) {
        return RequestDTO.<Q,B>builder().queryString(queryString).body(body).headers(header).build();
    }


    private <Q, B> String setURI(String url, RequestDTO<Q, B> requestDTO) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url);

        if (requestDTO.getQueryString() != null) {
            Map<String, Object> paramMap = objectMapper.convertValue(requestDTO.getQueryString(), new TypeReference<>() { });

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
        }

        return urlBuilder.build().toString();
    }

    private <Q, B> HttpEntity<B> setHttpEntity(RequestDTO<Q, B> requestDTO) {
        if (requestDTO == null) {
            return new HttpEntity<>(null, defaultJsonHeaders());
        }
        return new HttpEntity<>(this.nullChkBody(requestDTO), getOrDefaultHeaders(requestDTO.getHeaders()));
    }

    private <Q, B> B nullChkBody(RequestDTO<Q, B> requestDTO) {
        return requestDTO == null ? null : requestDTO.getBody();
    }

    private HttpHeaders defaultJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private HttpHeaders getOrDefaultHeaders(HttpHeaders header) {
        return header == null ? defaultJsonHeaders() : header;
    }

    public <T> void logStatusCode(ResponseEntity<T> responseEntity){
        log.info("[exchange] statusCode : {}", responseEntity.getStatusCode());
    }

    private void logErrorDetails(String exceptionType, HttpStatusCode statusCode, String statusText, String responseBody) {
        log.error("[{}] code: {}, statusText: {}, responseBody: {}",
                exceptionType, statusCode != null ? statusCode : "N/A", statusText, responseBody);
    }

    private void handleExchangeException(Exception e) {
        switch (e) {
            case HttpClientErrorException ex ->
                    logErrorDetails("HttpClientErrorException", ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
            case HttpServerErrorException ex ->
                    logErrorDetails("HttpServerErrorException", ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());
            case UnknownHttpStatusCodeException ex ->
                    logErrorDetails("UnknownHttpStatusCodeException", null, ex.getStatusText(), ex.getResponseBodyAsString());
            default -> log.error("[Exception] : {}", e.getMessage(), e);
        }
    }
}
