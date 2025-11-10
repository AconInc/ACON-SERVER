package com.acon.server.appintoss.api.controller;

import com.acon.server.appintoss.api.request.AppInTossSpotRequest;
import com.acon.server.appintoss.api.response.AppInTossSpotResponse;
import com.acon.server.appintoss.application.service.AppInTossService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/app-in-toss")
@Validated
public class AppInTossController {

    private final AppInTossService appInTossService;

    @PostMapping(
            path = "/spots",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public CompletableFuture<ResponseEntity<AppInTossSpotResponse>> postSpot(
            @Valid @RequestBody final AppInTossSpotRequest request
    ) {
        return appInTossService.postSpot(request)
                .thenApply(ResponseEntity::ok);
    }
}
