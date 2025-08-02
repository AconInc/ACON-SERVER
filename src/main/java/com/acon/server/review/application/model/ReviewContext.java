package com.acon.server.review.application.model;

import com.acon.server.member.domain.entity.Member;
import com.acon.server.review.domain.entity.Review;
import com.acon.server.spot.domain.entity.Spot;

public record ReviewContext(
        Member member,
        Spot spot,
        Review review
) {

    public static ReviewContext of(
            Member member,
            Spot spot,
            Review review
    ) {
        return new ReviewContext(member, spot, review);
    }
}
