package com.restclient.webclient.service;

import com.restclient.api.response.ApiResponse;
import com.restclient.member.request.MemberRequest;
import com.restclient.member.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebClientServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebClientService webClientService;

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

        ApiResponse<MemberResponse> response = webClientService.getWithParams(url, request, new ParameterizedTypeReference<>() {});

        assertEquals(response.getData().getId(), request.getId());

    }

    @Test
    void post() {

        url += "/1";

        ApiResponse<MemberResponse> response = webClientService.postWithBody(url, request, new ParameterizedTypeReference<>() {});
        assertEquals(response.getData().getId(), request.getId());

    }



}