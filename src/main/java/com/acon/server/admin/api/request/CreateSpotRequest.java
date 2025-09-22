package com.acon.server.admin.api.request;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.spot.domain.enums.SpotType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.springframework.util.StringUtils;

public record CreateSpotRequest(
        @NotBlank(message = "spotName은 공백일 수 없습니다.")
        String spotName,

        @NotBlank(message = "address는 공백일 수 없습니다.")
        String address,

        @NotNull(message = "localAcornCount는 필수입니다.")
        @Min(value = 0, message = "localAcornCount는 0 이상이어야 합니다.")
        Integer localAcornCount,

        @NotNull(message = "basicAcornCount는 필수입니다.")
        @Min(value = 0, message = "basicAcornCount는 0 이상이어야 합니다.")
        Integer basicAcornCount,

        @NotNull(message = "spotType은 필수입니다.")
        SpotType spotType,

        List<@NotBlank(message = "spotFeature는 공백일 수 없습니다.") String> spotFeatureList,

        @NotNull(message = "openingHourList는 필수입니다.")
        @Size(min = 7, max = 7, message = "영업 시간은 7일 모두 입력해야 합니다.")
        List<@Valid OpeningHourItem> openingHourList,

        @NotNull(message = "signatureMenuList는 필수입니다")
        @Size(min = 1, message = "대표 메뉴는 최소 1개 이상 입력해야 합니다.")
        List<@Valid SignatureMenu> signatureMenuList,

        String priceFeature,

        @Size(max = 10, message = "image는 최대 10장까지 업로드 가능합니다.")
        List<@NotBlank(message = "imageUrl은 공백일 수 없습니다.") String> menuboardImageList,

        @Size(max = 10, message = "image는 최대 10장까지 업로드 가능합니다.")
        List<@NotBlank(message = "imageUrl은 공백일 수 없습니다.") String> spotImageList
) {

    // Restaurant인 경우 spotFeatureList와 priceFeature 필수 검증
    public CreateSpotRequest {
        if (spotType == SpotType.RESTAURANT) {
            if (spotFeatureList == null || spotFeatureList.isEmpty()) {
                throw new BusinessException(ErrorType.MISSING_REQUIRED_FIELDS_ERROR);
            }
            if (!StringUtils.hasText(priceFeature)) {
                throw new BusinessException(ErrorType.MISSING_REQUIRED_FIELDS_ERROR);
            }
        }

        // 영업시간 요일 검증 (월~일 7일 모두 있어야 함)
        if (openingHourList != null && openingHourList.size() == 7) {
            Set<DayOfWeek> daySet = openingHourList.stream()
                    .map(OpeningHourItem::dayOfWeek)
                    .collect(java.util.stream.Collectors.toSet());

            if (daySet.size() != 7) {
                throw new BusinessException(ErrorType.MISSING_REQUIRED_FIELDS_ERROR);
            }
        }
    }

    public record OpeningHourItem(
            @NotNull(message = "dayOfWeek는 필수입니다.")
            DayOfWeek dayOfWeek,

            @NotNull(message = "closed는 필수입니다.")
            Boolean closed,

            LocalTime startTime,

            LocalTime endTime,

            LocalTime breakStartTime,

            LocalTime breakEndTime
    ) {

        public OpeningHourItem {
            if (!Boolean.TRUE.equals(closed)) {
                // 휴무일이 아닌 경우 startTime과 endTime 필수 검증
                if (startTime == null || endTime == null) {
                    throw new BusinessException(ErrorType.MISSING_REQUIRED_FIELDS_ERROR);
                }

                // break time 검증 (둘 다 있거나 둘 다 없어야 함)
                if ((breakStartTime == null && breakEndTime != null) ||
                        (breakStartTime != null && breakEndTime == null)) {
                    throw new BusinessException(ErrorType.MISSING_REQUIRED_FIELDS_ERROR);
                }
            }
        }
    }

    public record SignatureMenu(
            @NotBlank(message = "name은 공백일 수 없습니다.")
            String name,

            @NotNull(message = "price는 필수입니다.")
            @Min(value = -1, message = "price는 -1 이상이어야 합니다.")
            Integer price
    ) {

    }
}
