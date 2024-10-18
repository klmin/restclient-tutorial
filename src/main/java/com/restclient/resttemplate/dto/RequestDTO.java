package com.restclient.resttemplate.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Builder
@Getter
public class RequestDTO<Q, B>{

    private Q queryString;

    private B body;

    private HttpHeaders headers;
	
}
