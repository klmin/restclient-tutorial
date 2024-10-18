package com.restclient.resttemplate.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestRequestInterceptor implements ClientHttpRequestInterceptor{

    private static final int MAX_LOG_LENGTH = 1000;
	
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    	traceRequest(request, body);
    	ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }
    
    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("===========================rest request begin============================================");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Header      : {}", request.getHeaders() );
        log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
        log.info("============================rest request end=============================================");
    }
    
    private void traceResponse(ClientHttpResponse response) throws IOException {
        log.info("============================rest response begin==========================================");
        log.info("Status code  : {}", response.getStatusCode());
        log.info("Status text  : {}", response.getStatusText());
        log.info("Headers : {}", response.getHeaders());

        String responseBody = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());

        if (responseBody.length() > MAX_LOG_LENGTH) {
            log.info("Response body : {}... (truncated)", responseBody.substring(0, MAX_LOG_LENGTH));
        } else {
            log.info("Response body : {}", responseBody);
        }

        log.info("============================rest response end============================================");
    }


}
