package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record PreSignedUrlRequest(
        @NotBlank(message = "imageType은 공백일 수 없습니다.")
        String imageType,

        @NotBlank(message = "originalFileName은 공백일 수 없습니다.")
        String originalFileName
) {

}
