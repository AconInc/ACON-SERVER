package com.acon.server.review.api.controller;

import com.acon.server.review.api.request.ReviewRequestV1;
import com.acon.server.review.api.response.ReviewAvailabilityResponse;
import com.acon.server.review.application.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewControllerV1 {

    private final ReviewService reviewService;

    @GetMapping(path = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewAvailabilityResponse> getSpotReviewAvailability(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @RequestParam(name = "spotId") final Long spotId,
            @NotNull(message = "위도는 필수입니다.")
            @RequestParam(name = "latitude") Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @RequestParam(name = "longitude") Double longitude
    ) {
        if (reviewService.checkTestUser()) {
            latitude = 37.559115;
            longitude = 126.921976;
        }

        return ResponseEntity.ok(
                reviewService.verifyReviewAvailability(spotId, latitude, longitude)
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postReview(
            @Valid @RequestBody final ReviewRequestV1 request
    ) {
        reviewService.createReview(request.spotId(), request.acornCount());

        return ResponseEntity.ok().build();
    }
}
