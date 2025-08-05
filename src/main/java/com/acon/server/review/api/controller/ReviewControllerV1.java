package com.acon.server.review.api.controller;

import com.acon.server.review.api.request.ReviewRequestV1;
import com.acon.server.review.application.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
@Validated
public class ReviewControllerV1 {

    private final ReviewService reviewService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postReview(
            @Valid @RequestBody final ReviewRequestV1 request
    ) {
        reviewService.createReview(request.spotId(), request.acornCount());

        return ResponseEntity.ok().build();
    }
}
