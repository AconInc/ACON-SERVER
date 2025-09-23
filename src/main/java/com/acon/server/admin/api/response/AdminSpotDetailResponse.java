package com.acon.server.admin.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public record AdminSpotDetailResponse(
        String spotStatus,
        Long spotId,
        String userNickname,
        LocalDateTime updatedAt,
        String spotName,
        String address,
        Integer localAcornCount,
        Integer basicAcornCount,
        String spotType,
        List<String> spotFeatureList,
        List<OpeningHourItem> openingHourList,
        List<SignatureMenu> signatureMenuList,
        List<RecommendedMenu> recommendedMenuList,
        String priceFeature,
        List<String> menuboardImageList,
        List<String> spotImageList
) {

    public static AdminSpotDetailResponse of(
            String spotStatus,
            Long spotId,
            String userNickname,
            LocalDateTime updatedAt,
            String spotName,
            String address,
            Integer localAcornCount,
            Integer basicAcornCount,
            String spotType,
            List<String> spotFeatureList,
            List<OpeningHourItem> openingHourList,
            List<SignatureMenu> signatureMenuList,
            List<RecommendedMenu> recommendedMenuList,
            String priceFeature,
            List<String> menuboardImageList,
            List<String> spotImageList
    ) {
        return new AdminSpotDetailResponse(
                spotStatus,
                spotId,
                userNickname,
                updatedAt,
                spotName,
                address,
                localAcornCount,
                basicAcornCount,
                spotType,
                spotFeatureList,
                openingHourList,
                signatureMenuList,
                recommendedMenuList,
                priceFeature,
                menuboardImageList,
                spotImageList
        );
    }

    public record OpeningHourItem(
            DayOfWeek dayOfWeek,
            Boolean closed,
            LocalTime startTime,
            LocalTime endTime,
            LocalTime breakStartTime,
            LocalTime breakEndTime
    ) {

        public static OpeningHourItem of(
                DayOfWeek dayOfWeek,
                Boolean closed,
                LocalTime startTime,
                LocalTime endTime,
                LocalTime breakStartTime,
                LocalTime breakEndTime
        ) {
            return new OpeningHourItem(dayOfWeek, closed, startTime, endTime, breakStartTime, breakEndTime);
        }
    }

    public record SignatureMenu(
            String name,
            Integer price
    ) {

        public static SignatureMenu of(
                String name,
                Integer price
        ) {
            return new SignatureMenu(name, price);
        }
    }

    public record RecommendedMenu(
            String name,
            Integer recommendationCount
    ) {

        public static RecommendedMenu of(
                String name,
                Integer recommendationCount
        ) {
            return new RecommendedMenu(name, recommendationCount);
        }
    }
}
