package com.acon.server.admin.api.controller;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
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
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    @GetMapping(path = "/csrf", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CsrfTokenResponse> getCsrfToken(CsrfToken csrfToken) {
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
            @RequestParam(required = false) String query,
            @RequestParam(name = "queryTarget", required = false) String queryTargetString,
            @RequestParam(name = "status", required = false) List<String> status,
            @RequestParam(name = "missingField", required = false) String missingFieldString
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

    @GetMapping(path = "/spots/{spotId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getSpotDetail(@PathVariable Long spotId) {

        Map<String, Object> response = new HashMap<>();
        response.put("spotStatus", "PENDING");
        response.put("spotId", 1L);
        response.put("userNickname", "김성민");
        response.put("updatedAt", "2025-08-06T14:30:00");
        response.put("spotName", "커피 리브레 서교동");
        response.put("address", "서울 마포구 서교동 453-32");
        response.put("localAcornCount", 12);
        response.put("basicAcornCount", 40);
        response.put("spotType", "CAFE");
        response.put("spotFeature", "WORK_FRIENDLY");
        response.put("openingHourList", List.of(
                Map.of(
                        "dayOfWeek", "MONDAY",
                        "closed", false,
                        "startTime", "10:00",
                        "endTime", "22:00",
                        "breakStartTime", "15:00",
                        "breakEndTime", "16:00"
                ),
                Map.of(
                        "dayOfWeek", "TUESDAY",
                        "closed", false,
                        "startTime", "10:00",
                        "endTime", "22:00",
                        "breakStartTime", "15:00",
                        "breakEndTime", "16:00"
                ),
                Map.of(
                        "dayOfWeek", "WEDNESDAY",
                        "closed", false,
                        "startTime", "10:00",
                        "endTime", "22:00",
                        "breakStartTime", "15:00",
                        "breakEndTime", "16:00"
                ),
                Map.of(
                        "dayOfWeek", "THURSDAY",
                        "closed", false,
                        "startTime", "10:00",
                        "endTime", "22:00",
                        "breakStartTime", "15:00",
                        "breakEndTime", "16:00"
                ),
                Map.of(
                        "dayOfWeek", "FRIDAY",
                        "closed", false,
                        "startTime", "10:00",
                        "endTime", "23:00",
                        "breakStartTime", "15:00",
                        "breakEndTime", "16:00"
                ),
                Map.of(
                        "dayOfWeek", "SATURDAY",
                        "closed", false,
                        "startTime", "11:00",
                        "endTime", "23:00",
                        "breakStartTime", "16:00",
                        "breakEndTime", "17:00"
                ),
                Map.of(
                        "dayOfWeek", "SUNDAY",
                        "closed", true
                )
        ));
        response.put("signatureMenuList", List.of(
                Map.of(
                        "name", "콜드브루",
                        "price", 5500
                ),
                Map.of(
                        "name", "수제 바닐라라떼",
                        "price", 6200
                )
        ));
        response.put("recommendedMenuList", List.of(
                Map.of(
                        "name", "콜드브루",
                        "recommendationCount", 21
                ),
                Map.of(
                        "name", "말차라떼",
                        "recommendationCount", 12
                )
        ));
        response.put("menuboardImageList", List.of(
                "https://cdn.example.com/menu/menu_1_1.jpg",
                "https://cdn.example.com/menu/menu_1_2.jpg"
        ));
        response.put("spotImageList", List.of(
                "https://cdn.example.com/spot/spot_1_1.jpg",
                "https://cdn.example.com/spot/spot_1_2.jpg"
        ));

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/spots/{spotId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSpotDetail(
            @PathVariable Long spotId,
            @RequestBody Map<String, Object> updateRequest) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/spots/{spotId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSpotStatus(
            @PathVariable Long spotId,
            @RequestBody Map<String, String> statusRequest) {
        return ResponseEntity.ok().build();
    }
}
