package com.acon.server.member.domain.entity;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VerifiedArea {

    private final Long id;
    private final Long memberId;
    private final String name;
    private final LocalDateTime createdAt;

    @Builder
    public VerifiedArea(
            final Long id,
            final Long memberId,
            final String name,
            final LocalDateTime createdAt
    ) {
        this.id = id;
        this.memberId = memberId;
        this.name = name;
        this.createdAt = createdAt;
    }
}
