package com.restclient.member.request;

import com.restclient.member.response.MemberResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {

    private Long id;
    private String name;
    private Integer age;
    private List<String> hobby;
    private Map<String, Object> score;
    private LocalDateTime createdDttm;
    private LocalDate createdDt;

    public static MemberRequest create(Long id, String name, Integer age, List<String> hobby, Map<String, Object> score, LocalDateTime createdDttm
    , LocalDate createdDt) {
        return new MemberRequest(id, name, age, hobby, score, createdDttm, createdDt);
    }

    public MemberResponse toResponse() {
        return MemberResponse.builder()
                .id(id)
                .name(name)
                .age(age)
                .hobby(hobby)
                .score(score)
                .createdDttm(createdDttm)
                .createdDt(createdDt)
                .build();
    }

    public MemberResponse toResponse(Long id) {
        return MemberResponse.builder()
                .id(id)
                .name(name)
                .age(age)
                .hobby(hobby)
                .score(score)
                .createdDttm(createdDttm)
                .createdDt(createdDt)
                .build();
    }
}
