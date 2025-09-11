package com.acon.server.admin.api.controller;

import com.acon.server.admin.api.response.CsrfTokenResponse;
import com.acon.server.admin.api.response.DashboardResponse;
import com.acon.server.admin.application.service.AdminService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

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

    @GetMapping(path = "/spots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getSpots(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String queryTarget,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) String missingField) {

        List<Map<String, Object>> spotList = List.of(
                Map.of(
                        "id", 1L,
                        "userNickname", "김성민",
                        "spotName", "커피 리브레 서교동",
                        "spotStatus", "PENDING",
                        "spotType", "CAFE",
                        "updatedAt", "2025-08-06T10:00:00"
                ),
                Map.of(
                        "id", 2L,
                        "userNickname", "김시은",
                        "spotName", "프릳츠 커피 컴퍼니 도화점",
                        "spotStatus", "PENDING",
                        "spotType", "CAFE",
                        "updatedAt", "2025-08-06T10:15:00"
                ),
                Map.of(
                        "id", 3L,
                        "userNickname", "김유림",
                        "spotName", "빈브라더스 성수동",
                        "spotStatus", "PENDING",
                        "spotType", "CAFE",
                        "updatedAt", "2025-08-06T10:30:00"
                ),
                Map.of(
                        "id", 4L,
                        "userNickname", "김창균",
                        "spotName", "스시효 청담",
                        "spotStatus", "PENDING",
                        "spotType", "RESTAURANT",
                        "updatedAt", "2025-08-06T10:45:00"
                ),
                Map.of(
                        "id", 5L,
                        "userNickname", "박지현",
                        "spotName", "홍연참치 논현",
                        "spotStatus", "PENDING",
                        "spotType", "RESTAURANT",
                        "updatedAt", "2025-08-06T11:00:00"
                ),
                Map.of(
                        "id", 6L,
                        "userNickname", "이상일",
                        "spotName", "리틀넥 성수 브런치",
                        "spotStatus", "PENDING",
                        "spotType", "RESTAURANT",
                        "updatedAt", "2025-08-06T11:15:00"
                ),
                Map.of(
                        "id", 7L,
                        "userNickname", "이수민",
                        "spotName", "로리스 더 프라임 립 강남",
                        "spotStatus", "PENDING",
                        "spotType", "RESTAURANT",
                        "updatedAt", "2025-08-06T11:30:00"
                )
        );

        return ResponseEntity.ok(Map.of("spotList", spotList));
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
}
