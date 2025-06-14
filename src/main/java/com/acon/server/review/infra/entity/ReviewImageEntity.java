package com.acon.server.review.infra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_image")
public class ReviewImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_id", nullable = false)
    private Long spotId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "image", columnDefinition = "text", nullable = false)
    private String image;

    @Builder
    public ReviewImageEntity(Long id, Long spotId, Long reviewId, String image) {
        this.id = id;
        this.spotId = spotId;
        this.reviewId = reviewId;
        this.image = image;
    }
}
