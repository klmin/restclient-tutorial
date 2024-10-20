package com.restclient.restclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restclient.api.exception.ApiRuntimeException;
import com.restclient.restclient.client.TestClient;
import com.restclient.restclient.factory.HttpInterfaceFactory;
import com.restclient.restclient.property.TestProperties;
import com.restclient.resttemplate.interceptor.RestRequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestClientConfig {

    private final HttpClient httpClient;
    private final HttpInterfaceFactory httpInterfaceFactory;
    private final ObjectMapper objectMapper;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .messageConverters(
                        converters -> {
                            converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                            converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
                        })
                .requestInterceptor(new RestRequestInterceptor())
                .requestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient)))
                .build();
    }


    private RestClient createRestClient(String baseUrl, RestClient restClient) {
        return restClient.mutate()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            log.error("Client Error Code : {}", response.getStatusCode());
                            log.error("Client Error Message : {}", new String(response.getBody().readAllBytes()));
                            throw new ApiRuntimeException(HttpStatus.valueOf(response.getStatusCode().value()));
                        })
                .defaultStatusHandler(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            log.error("Server Error Code : {}", response.getStatusCode());
                            log.error("Server Error Message : {}", new String(response.getBody().readAllBytes()));
                            throw new ApiRuntimeException(HttpStatus.valueOf(response.getStatusCode().value()));
                        })
                .build();
    }

    @Bean
    public TestClient testClient(TestProperties testProperties){
        return httpInterfaceFactory.createClient(TestClient.class, createRestClient(testProperties.getUrl(), restClient()));
    }


}
