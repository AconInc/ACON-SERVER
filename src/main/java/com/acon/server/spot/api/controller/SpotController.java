package com.acon.server.spot.api.controller;

import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.spot.api.request.SpotListRequest;
import com.acon.server.spot.api.response.MenuboardImageListResponse;
import com.acon.server.spot.api.response.ReviewAvailabilityResponse;
import com.acon.server.spot.api.response.SearchSuggestionListResponse;
import com.acon.server.spot.api.response.SpotDetailResponse;
import com.acon.server.spot.api.response.SpotListResponse;
import com.acon.server.spot.api.response.SpotSearchListResponse;
import com.acon.server.spot.application.service.SpotService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class SpotController {

    private final SpotService spotService;
    private final PrincipalHandler principalHandler;

    @PostMapping(
            path = "/spots",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SpotListResponse> getRecommendedSpotList(
            @Valid @RequestBody final SpotListRequest request
    ) {
        // TODO: 추후 서비스단에서 검증하도록 변경 요망
        if (!principalHandler.isGuestUser() && spotService.checkTestUser()) {
            return ResponseEntity.ok(
                    spotService.fetchRecommendedSpotList(
                            new SpotListRequest(37.4940494, 127.030027, request.condition())
                    )
            );
        }

        return ResponseEntity.ok(
                spotService.fetchRecommendedSpotList(request)
        );
    }

    @GetMapping(path = "/spots/{spotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpotDetailResponse> getSpotDetail(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @PathVariable(name = "spotId") final Long spotId,
            @NotNull(message = "isDeepLink는 필수입니다.")
            @RequestParam(name = "isDeepLink") final Boolean isDeepLink
    ) {
        return ResponseEntity.ok(
                spotService.fetchSpotDetail(spotId, isDeepLink)
        );
    }

    @GetMapping(path = "/spots/{spotId}/menuboards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuboardImageListResponse> getMenuboardList(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @PathVariable(name = "spotId") final Long spotId
    ) {
        return ResponseEntity.ok(
                spotService.fetchMenuboards(spotId)
        );
    }

    @GetMapping(path = "/search-suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSuggestionListResponse> getSearchSuggestionListV1(
            @NotNull(message = "위도는 필수입니다.")
            @RequestParam(name = "latitude") Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @RequestParam(name = "longitude") Double longitude
    ) {
        if (spotService.checkTestUser()) {
            latitude = 37.559115;
            longitude = 126.921976;
        }

        return ResponseEntity.ok(
                spotService.fetchSearchSuggestions(latitude, longitude)
        );
    }

    @GetMapping(path = "/spots/search-suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSuggestionListResponse> getSearchSuggestionListV2(
            @NotNull(message = "위도는 필수입니다.")
            @RequestParam(name = "latitude") Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @RequestParam(name = "longitude") Double longitude
    ) {
        if (spotService.checkTestUser()) {
            latitude = 37.559115;
            longitude = 126.921976;
        }

        return ResponseEntity.ok(
                spotService.fetchSearchSuggestions(latitude, longitude)
        );
    }

    @GetMapping(path = "/spots/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpotSearchListResponse> getSpotSearchList(
            @RequestParam(value = "keyword", required = false) final String keyword
    ) {
        return ResponseEntity.ok(
                spotService.searchSpot(keyword)
        );
    }

    @GetMapping(path = "/spots/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewAvailabilityResponse> getSpotReviewAvailability(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @RequestParam(name = "spotId") final Long spotId,
            @NotNull(message = "위도는 필수입니다.")
            @RequestParam(name = "latitude") Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @RequestParam(name = "longitude") Double longitude
    ) {
        if (spotService.checkTestUser()) {
            latitude = 37.559115;
            longitude = 126.921976;
        }

        return ResponseEntity.ok(
                spotService.verifyReviewAvailability(spotId, latitude, longitude)
        );
    }

    @GetMapping(path = "/spots/{spotId}/distance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Double> getDistanceToSpot(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @PathVariable(name = "spotId") final Long spotId,
            @NotNull(message = "위도는 필수입니다.")
            @RequestParam(name = "latitude") final Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @RequestParam(name = "longitude") final Double longitude
    ) {
        return ResponseEntity.ok(
                spotService.calculateDistance(spotId, latitude, longitude)
        );
    }
}
