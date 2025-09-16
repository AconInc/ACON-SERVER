package com.acon.server.member.api.request;

import com.acon.server.member.domain.enums.ImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PreSignedUrlRequest(
        @NotNull(message = "imageType은 필수입니다.")
        ImageType imageType,

        @NotBlank(message = "originalFileName은 공백일 수 없습니다.")
        String originalFileName
) {

}
