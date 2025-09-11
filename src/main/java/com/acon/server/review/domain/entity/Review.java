package com.acon.server.review.domain.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Review {

    private final Long id;
    private final Long spotId;
    private final Long memberId;
    private final String recommendedMenu;
    private final Integer acornCount;
    private final Boolean localAcorn;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Review(
            Long id,
            Long spotId,
            Long memberId,
            String recommendedMenu,
            Integer acornCount,
            Boolean localAcorn,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.spotId = spotId;
        this.memberId = memberId;
        this.recommendedMenu = recommendedMenu;
        this.acornCount = acornCount;
        this.localAcorn = localAcorn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
