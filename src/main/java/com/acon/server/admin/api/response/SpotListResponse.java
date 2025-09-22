package com.acon.server.admin.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record SpotListResponse(
        List<SpotItem> spotList
) {

    public static SpotListResponse of(List<SpotItem> spotList) {
        return new SpotListResponse(spotList);
    }

    public record SpotItem(
            Long id,
            String userNickname,
            String spotName,
            String spotStatus,
            String spotType,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime updatedAt
    ) {

        public static SpotItem of(
                Long id,
                String userNickname,
                String spotName,
                String spotStatus,
                String spotType,
                LocalDateTime updatedAt
        ) {
            return new SpotItem(id, userNickname, spotName, spotStatus, spotType, updatedAt);
        }
    }
}
