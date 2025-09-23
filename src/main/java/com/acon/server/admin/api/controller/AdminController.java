package com.acon.server.admin.api.controller;

import com.acon.server.admin.api.request.CreateSpotRequest;
import com.acon.server.admin.api.request.UpdateSpotDetailRequest;
import com.acon.server.admin.api.response.AdminSpotDetailResponse;
import com.acon.server.admin.api.response.CsrfTokenResponse;
import com.acon.server.admin.api.response.DashboardResponse;
import com.acon.server.admin.api.response.SpotListResponse;
import com.acon.server.admin.application.service.AdminService;
import com.acon.server.admin.domain.enums.MissingField;
import com.acon.server.admin.domain.enums.QueryTarget;
import com.acon.server.member.api.request.PreSignedUrlRequest;
import com.acon.server.member.api.response.PreSignedUrlResponse;
import com.acon.server.member.application.service.MemberService;
import com.acon.server.spot.domain.enums.SpotStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Validated
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    @GetMapping(path = "/csrf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CsrfTokenResponse> getCsrfToken(
            final CsrfToken csrfToken
    ) {
        return ResponseEntity.ok(
                CsrfTokenResponse.of(
                        csrfToken.getHeaderName(),
                        csrfToken.getParameterName(),
                        csrfToken.getToken()
                )
        );
    }

    @GetMapping(path = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @PostMapping(
            path = "/images/presigned-url",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PreSignedUrlResponse> createPreSignedUrl(
            @Valid @RequestBody final PreSignedUrlRequest request
    ) {
        return ResponseEntity.ok(
                memberService.createPreSignedUrl(request.imageType(), request.originalFileName())
        );
    }

    @GetMapping(path = "/spots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SpotListResponse> getSpots(
            @RequestParam(required = false) final String query,
            @RequestParam(name = "queryTarget", required = false) final String queryTargetString,
            @RequestParam(name = "status", required = false) final List<String> status,
            @RequestParam(name = "missingField", required = false) final String missingFieldString
    ) {
        QueryTarget queryTarget = queryTargetString != null ? QueryTarget.fromValue(queryTargetString) : null;
        List<SpotStatus> spotStatusList = (status != null)
                ? status.stream()
                .filter(Objects::nonNull)
                .map(SpotStatus::fromValue)
                .toList()
                : List.of();
        MissingField missingField = missingFieldString != null ? MissingField.fromValue(missingFieldString) : null;

        return ResponseEntity.ok(
                adminService.getSpots(query, queryTarget, spotStatusList, missingField)
        );
    }

    @PostMapping(path = "/spots", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postSpot(
            @Valid @RequestBody final CreateSpotRequest request
    ) {
        adminService.createSpot(request);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/spots/{spotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminSpotDetailResponse> getSpotDetail(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @PathVariable final Long spotId
    ) {
        return ResponseEntity.ok(
                adminService.getSpotDetail(spotId)
        );
    }

    @PatchMapping(path = "/spots/{spotId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSpotDetail(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @PathVariable final Long spotId,

            @Valid @RequestBody final UpdateSpotDetailRequest request
    ) {
        adminService.updateSpotDetail(spotId, request);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/spots/{spotId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSpotStatus(
            @PathVariable Long spotId,
            @RequestBody Map<String, String> statusRequest) {
        return ResponseEntity.ok().build();
    }
}
