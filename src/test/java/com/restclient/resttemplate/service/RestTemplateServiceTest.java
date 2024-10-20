package com.restclient.resttemplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restclient.api.response.ApiResponse;
import com.restclient.member.request.MemberRequest;
import com.restclient.member.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestTemplateServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplateService restTemplateService;

    @Autowired
    private ObjectMapper objectMapper;

    private String url;
    private MemberRequest request;

    @BeforeEach
    public void init(){
        url = "http://localhost:" + port + "/members";
        LocalDateTime now = LocalDateTime.now();
        request = MemberRequest.create(1L, "테스트", 20, List.of("영화감상","운동"),
                Map.of("수학", 80, "영어", 70), now, now.toLocalDate());
    }

    @Test
    void get(){
        ApiResponse<MemberResponse> response = restTemplateService.exchange(url, HttpMethod.GET, null, request, null, new ParameterizedTypeReference<>() {});

        assertTrue(response.isResult());
        assertEquals(request.getAge(), response.getData().getAge());
        assertEquals(request.getCreatedDt(), response.getData().getCreatedDt());
    }

    @Test
    void post(){
        url += "/1";
        ApiResponse<MemberResponse> response = restTemplateService.exchange(url, HttpMethod.POST, null, null, request, new ParameterizedTypeReference<>() {});
        assertTrue(response.isResult());
        assertEquals(request.getName(), response.getData().getName());
        assertEquals(request.getId(), response.getData().getId());
    }
    @Test
    void patch(){
        url += "/1";
        ApiResponse<MemberResponse> response = restTemplateService.exchange(url, HttpMethod.PATCH, null, null, request, new ParameterizedTypeReference<>() {});
        assertTrue(response.isResult());
        assertEquals(request.getScore().size(), response.getData().getScore().size());
        assertEquals(request.getHobby().size(), response.getData().getHobby().size());
    }

    @Test
    void put(){
        url += "/1";
        ApiResponse<MemberResponse> response = restTemplateService.exchange(url, HttpMethod.PUT, null, null, request, new ParameterizedTypeReference<>() {});
        assertTrue(response.isResult());
        assertEquals(request.getId(), response.getData().getId());
        assertEquals(request.getAge(), response.getData().getAge());
    }

    @Test
    void delete(){
        url += "/1";
        ApiResponse<MemberResponse> response = restTemplateService.exchange(url, HttpMethod.DELETE, null, null, null, new ParameterizedTypeReference<>() {});
        assertTrue(response.isResult());
        assertEquals(request.getId(), response.getData().getId());
    }


}