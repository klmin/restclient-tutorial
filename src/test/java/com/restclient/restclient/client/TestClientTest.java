package com.restclient.restclient.client;

import com.restclient.api.response.ApiResponse;
import com.restclient.member.request.MemberRequest;
import com.restclient.member.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TestClientTest {

    @Autowired
    private TestClient testClient;

    private MemberRequest request;

    @BeforeEach
    public void init(){
        LocalDateTime now = LocalDateTime.now();
        request = MemberRequest.create(1L, "테스트", 20, List.of("영화감상","운동"),
                Map.of("수학", 80, "영어", 70), now, now.toLocalDate());
    }

    @Test
    void get() {
        Long id = 1L;
        String name = "테스트";
        Integer age = 20;
        List<String> list = List.of("영화감상","운동");
        Map<String, Object> map = Map.of("수학", 80, "영어", 70);
        LocalDateTime now = LocalDateTime.now();

        ResponseEntity<ApiResponse<MemberResponse>> response = testClient.get(id, name, age, list, map, now, now.toLocalDate());
        assert response.getBody() != null;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isResult());
        assertEquals(response.getBody().getData().getId(), id);
        assertEquals(response.getBody().getData().getAge(), age);

    }

    @Test
    void post() {

        Long id = 1L;

        ResponseEntity<ApiResponse<MemberResponse>> response = testClient.post(id, request);
        assert response.getBody() != null;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isResult());
        assertEquals(response.getBody().getData().getId(), id);
        assertEquals(response.getBody().getData().getAge(), request.getAge());

        ApiResponse<MemberResponse> responseBody = testClient.postBody(id, request);
        assertTrue(responseBody.isResult());
        assertEquals(responseBody.getData().getId(), id);
        assertEquals(responseBody.getData().getAge(), request.getAge());

    }

    @Test
    void put() {
        Long id = 1L;
        ResponseEntity<ApiResponse<MemberResponse>> response = testClient.put(id, request);
        assert response.getBody() != null;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isResult());
        assertEquals(response.getBody().getData().getId(), id);
        assertEquals(response.getBody().getData().getAge(), request.getAge());
    }

    @Test
    void patch() {

        Long id = 1L;
        ResponseEntity<ApiResponse<MemberResponse>> response = testClient.patch(id, request);
        assert response.getBody() != null;
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(response.getBody().isResult());
        assertEquals(response.getBody().getData().getId(), id);
        assertEquals(response.getBody().getData().getAge(), request.getAge());
    }

    @Test
    void delete() {

        Long id = 1L;
        ResponseEntity<ApiResponse<MemberResponse>> response = testClient.delete(id);
        assert response.getBody() != null;
        assertEquals(response.getBody().getData().getId(), id);
    }


}