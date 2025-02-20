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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
        // TODO: QA를 위한 임시 위치 정보 할당-1, 추후 삭제 요망
        SpotListRequest spotListRequest = new SpotListRequest(37.559115, 126.921976, request.condition());

        return ResponseEntity.ok(
                spotService.fetchRecommendedSpotList(spotListRequest)
        );
    }

    @GetMapping(path = "/spots/{spotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpotDetailResponse> getSpotDetail(
            @Positive(message = "spotId는 양수여야 합니다.")
            @Validated @PathVariable(name = "spotId") final Long spotId
    ) {
        return ResponseEntity.ok(
                spotService.fetchSpotDetail(spotId)
        );
    }

    @GetMapping(path = "/spots/{spotId}/menus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MenuListResponse> getMenus(
            @Positive(message = "spotId는 양수여야 합니다.")
            @Validated @PathVariable(name = "spotId") final Long spotId
    ) {
        return ResponseEntity.ok(
                spotService.fetchMenus(spotId)
        );
    }

    @GetMapping(path = "/search-suggestions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchSuggestionListResponse> getSearchSuggestions(
            @DecimalMin(value = "33.1", message = "위도는 최소 33.1°N 이상이어야 합니다.(대한민국 기준)")
            @DecimalMax(value = "38.6", message = "위도는 최대 38.6°N 이하이어야 합니다.(대한민국 기준)")
            @Validated @RequestParam(name = "latitude") final Double latitude,
            @DecimalMin(value = "124.6", message = "경도는 최소 124.6°E 이상이어야 합니다.(대한민국 기준)")
            @DecimalMax(value = "131.9", message = "경도는 최대 131.9°E 이하이어야 합니다.(대한민국 기준)")
            @Validated @RequestParam(name = "longitude") final Double longitude
    ) {
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
            @Positive(message = "spotId는 양수여야 합니다.")
            @Validated @RequestParam(name = "spotId") final Long spotId,
            @Validated @RequestParam(name = "longitude") final Double longitude,
            @Validated @RequestParam(name = "latitude") final Double latitude
    ) {
        // TODO: QA를 위한 임시 위치 정보 할당-2, 추후 삭제 요망
        return ResponseEntity.ok(
                new VerifiedSpotResponse(spotService.verifySpot(spotId, 126.921976, 37.559115))
        );
    }
}
