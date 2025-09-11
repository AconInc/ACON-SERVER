package com.acon.server.admin.api.controller;

import com.acon.server.admin.api.response.CsrfTokenResponse;
import com.acon.server.admin.api.response.DashboardResponse;
import com.acon.server.admin.application.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
