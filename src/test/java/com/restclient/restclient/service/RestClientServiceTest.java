package com.restclient.restclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restclient.api.exception.ApiRuntimeException;
import com.restclient.api.response.ApiResponse;
import com.restclient.member.request.MemberRequest;
import com.restclient.member.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestClientServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;
    MemberRequest request;

    @BeforeEach
    public void init(){
        url = "http://localhost:" + port + "/members";
        request = MemberRequest.create(1L, "테스트", 20, List.of("영화감상","운동"),
                Map.of("수학", 80, "영어", 70));
    }

    @Test
    void get(){

        String fullUrl = buildUriWithParams(url, request);

        ResponseEntity<String> response = restClient.get()
                .uri(fullUrl)
                .retrieve()
                .toEntity(String.class);

        System.out.println("resposne.body : "+response.getBody());

        String response1 = restClient.get()
                .uri(fullUrl)
                .retrieve()
                .body(String.class);

        System.out.println("response : "+response1);

        ResponseEntity<ApiResponse<MemberResponse>> response2 = restClient.get()
                .uri(fullUrl)
                .accept(APPLICATION_JSON)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        System.out.println("response2 : "+response2.getBody().getData().getName());
    }

    @Test
    void post() throws JsonProcessingException {

        url += "/1";

        ResponseEntity<ApiResponse<MemberResponse>> response = restClient.post()
                .uri(url)
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});


        String failUrl = url+"/1L";

        try {
            restClient.post()
                    .uri(failUrl)
                    .contentType(APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ApiRuntimeException(HttpStatus.valueOf(res.getStatusCode().value()), res.getStatusText());
                    })
                    .toEntity(new ParameterizedTypeReference<>() {
                    });
        }catch(Exception e){
            System.out.println("e.getMessage : "+e.getMessage());
        }

        try {

        ApiResponse<MemberResponse> response1 = restClient.post()
                .uri(failUrl)
                .contentType(APPLICATION_JSON)
                .body(request)
                .exchange((req, res) -> {
                    if (res.getStatusCode().is4xxClientError()) {
                        throw new ApiRuntimeException(HttpStatus.valueOf(res.getStatusCode().value()), res.getStatusText());
                    }
                    else {
                        return objectMapper.readValue(res.getBody(), new TypeReference<>() {});
                    }
                });
        }catch(Exception e){
            System.out.println("e.getMessage : "+e.getMessage());
        }

    }

    @Test
    void patch(){

        url += "/1";

        ApiResponse<MemberResponse> response = restClient.patch()
                .uri(url)
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        System.out.println("response : "+ response.getData().getName());
    }

    @Test
    void put(){

        url += "/1";

        ApiResponse<MemberResponse> response = restClient.put()
                .uri(url)
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        System.out.println("response : "+ response.getData().getName());
    }

    @Test
    void delete(){
        url += "/{id}";

        ResponseEntity<Void> response = restClient.delete()
                .uri(url, 1)
                .retrieve()
                .toBodilessEntity();

        System.out.println("response : "+ response.getBody());
        System.out.println("response : "+ response.getStatusCode());
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