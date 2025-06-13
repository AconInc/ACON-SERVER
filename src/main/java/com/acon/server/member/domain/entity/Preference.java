package com.acon.server.member.domain.entity;

import com.acon.server.member.domain.enums.DislikeFood;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Preference {

    private final Long memberId;
    private final List<DislikeFood> dislikeFoodList;

    @Builder
    public Preference(
            Long memberId,
            List<DislikeFood> dislikeFoodList
    ) {
        this.memberId = memberId;
        this.dislikeFoodList = dislikeFoodList;
    }
}
