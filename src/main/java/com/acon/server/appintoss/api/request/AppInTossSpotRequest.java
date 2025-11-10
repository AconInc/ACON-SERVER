package com.acon.server.appintoss.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AppInTossSpotRequest(
        @NotBlank(message = "userInput은 공백일 수 없습니다.")
        @Size(min = 1, max = 100, message = "userInput은 1자 이상 100자 이하이어야 합니다.")
        String userInput
) {

}
