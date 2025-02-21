package com.acon.server.spot.api.controller;

import com.acon.server.spot.api.request.SpotListRequest;
import com.acon.server.spot.api.response.MenuListResponse;
import com.acon.server.spot.api.response.SearchSpotListResponse;
import com.acon.server.spot.api.response.SearchSuggestionListResponse;
import com.acon.server.spot.api.response.SpotDetailResponse;
import com.acon.server.spot.api.response.SpotListResponse;
import com.acon.server.spot.api.response.VerifiedSpotResponse;
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
public class SpotController {

    private final SpotService spotService;

    @PostMapping(
            path = "/spots",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<SpotListResponse> getRecommendedSpotList(
            @Valid @RequestBody final SpotListRequest request
    ) {
        if (spotService.checkTestUser()) {
            return ResponseEntity.ok(
                    spotService.fetchRecommendedSpotList(
                            new SpotListRequest(37.559115, 126.921976, request.condition())
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
            @Validated @PathVariable(name = "spotId") final Long spotId
    ) {
        return ResponseEntity.ok(
                spotService.fetchSpotDetail(spotId)
        );
    }

    @GetMapping(path = "/spots/{spotId}/menus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuListResponse> getMenus(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @Validated @PathVariable(name = "spotId") final Long spotId
    ) {
        return ResponseEntity.ok(
                spotService.fetchMenus(spotId)
        );
    }

    @GetMapping(path = "/search-suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSuggestionListResponse> getSearchSuggestions(
            @NotNull(message = "위도는 필수입니다.")
            @Validated @RequestParam(name = "latitude") Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @Validated @RequestParam(name = "longitude") Double longitude
    ) {
        if (spotService.checkTestUser()) {
            latitude = 37.559115;
            longitude = 126.921976;
        }

        return ResponseEntity.ok(
                spotService.fetchSearchSuggestions(latitude, longitude)
        );
    }

    // TODO: 메서드 네이밍 수정 필요
    @GetMapping(path = "/spots/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSpotListResponse> searchSpot(
            @RequestParam(value = "keyword", required = false) final String keyword
    ) {
        return ResponseEntity.ok(
                spotService.searchSpot(keyword)
        );
    }

    // TODO: 메서드 네이밍 수정 필요
    @GetMapping(path = "/spots/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifiedSpotResponse> verifySpot(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @Validated @RequestParam(name = "spotId") final Long spotId,
            @NotNull(message = "위도는 필수입니다.")
            @Validated @RequestParam(name = "latitude") Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @Validated @RequestParam(name = "longitude") Double longitude
    ) {
        if (spotService.checkTestUser()) {
            latitude = 37.559115;
            longitude = 126.921976;
        }

        return ResponseEntity.ok(
                new VerifiedSpotResponse(spotService.verifySpot(spotId, latitude, longitude))
        );
    }
}
