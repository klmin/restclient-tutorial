package com.restclient.member.controller;

import com.restclient.api.response.ApiResponse;
import com.restclient.member.request.MemberRequest;
import com.restclient.member.response.MemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/members")
@RestController
public class MemberController {

    @GetMapping
    public ResponseEntity<ApiResponse<MemberResponse>> get(@ModelAttribute MemberRequest request) {
        return ApiResponse.success(request.toResponse());
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> post(@PathVariable Long id, @RequestBody MemberRequest request) {
        return ApiResponse.success(request.toResponse(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> put(@PathVariable Long id, @RequestBody MemberRequest request) {
        return ApiResponse.success(request.toResponse(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> patch(@PathVariable Long id, @RequestBody MemberRequest request) {
        return ApiResponse.success(request.toResponse(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> delete(@PathVariable Long id) {
        return ApiResponse.success(MemberResponse.builder().id(id).build());
    }
}
