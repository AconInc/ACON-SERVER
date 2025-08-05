package com.acon.server.spot.api.controller;

import com.acon.server.spot.api.response.SearchSuggestionListResponse;
import com.acon.server.spot.application.service.SpotService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
@Validated
public class SpotControllerV2 {

    private final SpotService spotService;

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
}
