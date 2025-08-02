package com.acon.server.review.api.controller;

import com.acon.server.review.api.request.ReviewRequestV2;
import com.acon.server.review.application.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/reviews")
public class ReviewControllerV2 {

    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postReview(
            @Valid @RequestBody final ReviewRequestV2 request
    ) {
        reviewService.createReview(request.spotId(), request.recommendedMenu().trim(), request.acornCount());

        return ResponseEntity.ok().build();
    }
}
