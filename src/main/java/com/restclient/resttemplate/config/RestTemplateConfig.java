package com.restclient.resttemplate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restclient.resttemplate.interceptor.RestRequestInterceptor;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.DefaultBackoffStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    public HttpClient httpClient(){

        int maxPool = 200; // 전체 커넥션 풀
        int maxPerRoute = 100; // 호스트 당 커넥션 풀에서 쓸 수 있는 커넥션 개수
        int idleConnectionTimeoutSec = 30; // 유휴 커넥션이 이 시간이 지나면 커넥션 반납
        long requestTimeOut = 5000; // 커넥션 풀로부터 커넥션 받기를 대기하는 시간
        long readTimeOut = 5000; // 서버로부터 응답을 받기 위한 최대 대기 시간
        int retryCount = 1; // 어떠한 예외로 인해 처리하지 못한 요청에 대한 retry 횟수
        long backoff = 1000; // retry 간 back off

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(maxPool);
        connManager.setDefaultMaxPerRoute(maxPerRoute);
        connManager.setDefaultSocketConfig(SocketConfig.DEFAULT);
        connManager.setDefaultConnectionConfig(ConnectionConfig.custom().setValidateAfterInactivity(Timeout.ofSeconds(5)).build());

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(requestTimeOut)) // 연결 타임아웃
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeOut)) // 읽기 타임아웃
                .build();

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionBackoffStrategy(new DefaultBackoffStrategy()) //어떤 상황에서 백오프를 할지(위 옵션과 매칭되는 옵션)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()) //default 값 서버에서 받은 keep-alive
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(retryCount, TimeValue.ofMilliseconds(backoff))) // 몇번 재시도 할지
                .evictIdleConnections(TimeValue.ofSeconds(idleConnectionTimeoutSec)) // 유휴 커넥션이 이 시간(초)동안 존재하면 커넥션 종료
                .build();
    }

    @Bean
    public RestTemplate restTemplate(ObjectMapper objectMapper){

        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
        restTemplate.getMessageConverters().add(converter);

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new RestRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
        return restTemplate;


    }
}
