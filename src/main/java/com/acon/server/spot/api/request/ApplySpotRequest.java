package com.acon.server.spot.api.request;

import com.acon.server.spot.domain.enums.SpotType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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

        @Valid List<Feature> featureList,

        @NotBlank(message = "recommendedMenu는 공백일 수 없습니다.")
        @Size(min = 1, max = 30, message = "recommendedMenu는 1자 이상 30자 이하이어야 합니다.")
        String recommendedMenu,

        @Size(max = 10, message = "image는 최대 10장까지 업로드 가능합니다.")
        List<@NotBlank(message = "imageUrl은 공백일 수 없습니다.") String> imageList
) {

    public record Feature(
            @NotBlank(message = "category는 공백일 수 없습니다.")
            String category,

            @NotEmpty(message = "optionList는 비어 있을 수 없습니다.")
            List<@NotBlank(message = "option은 공백일 수 없습니다.") String> optionList
    ) {

    }

    @AssertTrue(message = "spotType이 CAFE가 아닐 경우, featureList는 비어 있을 수 없습니다.")
    private boolean featureListRequiredWhenNotCafe() {
        if (isCafe(spotType)) {
            return true;
        }

        return hasElements(featureList);
    }

    private static boolean isCafe(String value) {
        return SpotType.CAFE.name().equalsIgnoreCase(value);
    }

    private static boolean hasElements(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
