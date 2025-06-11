package com.acon.server.member.domain.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GuidedSpot {

    private final Long id;
    private final Long memberId;
    private final Long spotId;
    private final LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public GuidedSpot(
            final Long id,
            final Long memberId,
            final Long spotId,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.spotId = spotId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setUpdatedAtNow() {
        this.updatedAt = LocalDateTime.now();
    }
}
