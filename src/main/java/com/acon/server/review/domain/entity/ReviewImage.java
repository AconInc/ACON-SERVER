package com.acon.server.review.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewImage {

    private final Long id;
    private final Long spotId;
    private final Long reviewId;
    private final String image;

    @Builder
    public ReviewImage(
            Long id,
            Long spotId,
            Long reviewId,
            String image
    ) {
        this.id = id;
        this.spotId = spotId;
        this.reviewId = reviewId;
        this.image = image;
    }
}
