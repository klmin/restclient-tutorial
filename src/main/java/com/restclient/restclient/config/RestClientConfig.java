package com.restclient.restclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restclient.resttemplate.interceptor.RestRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(ObjectMapper objectMapper){
        return RestClient.builder()
                .messageConverters(
                        converters -> {
                            converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
                            converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
                        })
                .build();
    }
}
