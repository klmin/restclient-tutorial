package com.restclient.httpclient.config;

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

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient(){

        int maxPool = 200; // 전체 커넥션 풀에서 사용할 수 있는 최대 커넥션 수
        int maxPerRoute = 100; // 각 호스트(서버)당 커넥션 풀에서 사용할 수 있는 최대 커넥션 수
        long validateAfterInactivity = 5L; // 유휴상태 검증 시간
        int idleConnectionTimeoutSec = 30; // 유휴 커넥션이 이 시간이 지나면 풀에서 제거되는 시간
        long requestTimeOut = 5000L; // 커넥션 풀에서 커넥션을 얻기 위해 대기할 수 있는 최대 시간
        long readTimeOut = 5000L; // 서버로부터 응답을 기다리는 최대 시간
        int retryCount = 1; // 예외가 발생했을 때 요청을 재시도할 최대 횟수
        long backoff = 1000; // 재시도 간의 대기 시간

        // 커넥션 관리 매니저 (커넥션 풀을 관리하는 매니저)
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(maxPool);
        connManager.setDefaultMaxPerRoute(maxPerRoute);
        connManager.setDefaultSocketConfig(SocketConfig.DEFAULT); // 기본 TCP 소켓 설정
        connManager.setDefaultConnectionConfig(ConnectionConfig
                .custom()
                .setValidateAfterInactivity(Timeout.ofSeconds(validateAfterInactivity))
                .build()); // 유휴 상태에서 커넥션을 재사용하기 전에 유효성 검사를 수행 (서버와의 연결이 끊겼는지 확인)


        // 요청 관련 다양한 설정을 제공 (타임아웃 관련 설정)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(requestTimeOut))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeOut))
                .build();

        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionBackoffStrategy(new DefaultBackoffStrategy()) // 네트워크 오류 발생 시 재시도 간 백오프전략
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()) // 서버가 보낸 Keep-Alive 헤더 값에 따라 커넥션을 유지하는 전략
                .setRetryStrategy(new DefaultHttpRequestRetryStrategy(retryCount, TimeValue.ofMilliseconds(backoff))) // 네트워크 오류 또는 일시적인 서버 문제 발생 시 재시도 전략
                .evictIdleConnections(TimeValue.ofSeconds(idleConnectionTimeoutSec)) // 유휴 커넥션이 사용되지 않았을 경우 커넥션을 종료하고 풀에서 제거
                .build();
    }
}
