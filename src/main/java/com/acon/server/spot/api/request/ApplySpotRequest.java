package com.acon.server.spot.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ApplySpotRequest(
        @NotBlank(message = "spotName은 공백일 수 없습니다.")
        @Size(min = 1, max = 20, message = "spotName은 1자 이상 20자 이하이어야 합니다.")
        String spotName,

        @NotBlank(message = "address는 공백일 수 없습니다.")
        @Size(min = 1, max = 100, message = "address는 1자 이상 100자 이하이어야 합니다.")
        String address,

        @NotBlank(message = "spotType은 공백일 수 없습니다.")
        String spotType,

        @NotEmpty(message = "featureList는 비어 있을 수 없습니다.")
        @Valid List<Feature> featureList,

        @NotBlank(message = "recommendedMenu는 공백일 수 없습니다.")
        @Size(min = 1, max = 30, message = "recommendedMenu는 1자 이상 30자 이하이어야 합니다.")
        String recommendedMenu,

        List<@NotBlank(message = "imageUrl은 공백일 수 없습니다.") String> imageList
) {

    public record Feature(
            @NotBlank(message = "category는 공백일 수 없습니다.")
            String category,

            @NotEmpty(message = "optionList는 비어 있을 수 없습니다.")
            List<@NotBlank(message = "option은 공백일 수 없습니다.") String> optionList
    ) {

    }
}
