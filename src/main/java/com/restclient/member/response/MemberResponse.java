package com.restclient.member.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberResponse {

    private Long id;
    private String name;
    private Integer age;
    private List<String> hobby;
    private Map<String, Object> score;
}
