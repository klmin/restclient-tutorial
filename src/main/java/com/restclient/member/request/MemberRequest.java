package com.restclient.member.request;

import com.restclient.member.response.MemberResponse;
import lombok.*;

import java.lang.reflect.Member;
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

    public static MemberRequest create(Long id, String name, Integer age, List<String> hobby, Map<String, Object> score) {
        return new MemberRequest(id, name, age, hobby, score);
    }

    public MemberResponse toResponse() {
        return MemberResponse.builder()
                .id(id)
                .name(name)
                .age(age)
                .hobby(hobby)
                .score(score)
                .build();
    }

    public MemberResponse toResponse(Long id) {
        return MemberResponse.builder()
                .id(id)
                .name(name)
                .age(age)
                .hobby(hobby)
                .score(score)
                .build();
    }
}
