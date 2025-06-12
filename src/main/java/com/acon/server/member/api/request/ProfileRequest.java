package com.acon.server.member.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(Include.NON_NULL)
public record ProfileRequest(
        String profileImage,
        @NotBlank(message = "nickname은 공백일 수 없습니다.")
        String nickname,
        String birthDate
) {

}
