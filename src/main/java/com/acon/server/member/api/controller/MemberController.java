package com.acon.server.member.api.controller;

import com.acon.server.member.api.request.GuidedSpotRequest;
import com.acon.server.member.api.request.LoginRequest;
import com.acon.server.member.api.request.LogoutRequest;
import com.acon.server.member.api.request.PreferenceRequest;
import com.acon.server.member.api.request.ProfileRequest;
import com.acon.server.member.api.request.ReissueTokenRequest;
import com.acon.server.member.api.request.ReplaceVerifiedAreaRequest;
import com.acon.server.member.api.request.SavedSpotRequest;
import com.acon.server.member.api.request.VerifiedAreaRequest;
import com.acon.server.member.api.request.WithdrawalReasonRequest;
import com.acon.server.member.api.response.AcornCountResponse;
import com.acon.server.member.api.response.AppUpdateResponse;
import com.acon.server.member.api.response.LoginResponse;
import com.acon.server.member.api.response.PreSignedUrlResponse;
import com.acon.server.member.api.response.ProfileResponse;
import com.acon.server.member.api.response.ReissueTokenResponse;
import com.acon.server.member.api.response.SavedSpotListResponse;
import com.acon.server.member.api.response.VerifiedAreaListResponse;
import com.acon.server.member.application.service.MemberService;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.domain.enums.ImageType;
import com.acon.server.member.domain.enums.Platform;
import com.acon.server.member.domain.enums.SocialType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class MemberController {

    private final MemberService memberService;

    @GetMapping(path = "/app-updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUpdateResponse> getForceUpdateRequired(
            @RequestParam(name = "platform") final String platformString,
            @Pattern(regexp = "^\\d+(\\.\\d+){0,2}$", message = "유효하지 않은 version입니다.")
            @RequestParam(name = "version") final String version
    ) {
        Platform platform = Platform.fromValue(platformString);

        return ResponseEntity.ok(
                memberService.fetchForceUpdateRequired(platform, version)
        );
    }

    @PostMapping(
            path = "/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody final LoginRequest request
    ) {
        SocialType socialType = SocialType.fromValue(request.socialType());

        return ResponseEntity.ok(
                memberService.login(socialType, request.idToken())
        );
    }

    @PostMapping(path = "/verified-areas", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postVerifiedArea(
            @Valid @RequestBody final VerifiedAreaRequest request
    ) {
        if (memberService.checkTestUser()) {
            memberService.createVerifiedArea(37.559115, 126.921976);
        } else {
            memberService.createVerifiedArea(request.latitude(), request.longitude());
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/preference", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> putPreference(
            @Valid @RequestBody final PreferenceRequest request
    ) {
        List<DislikeFood> dislikeFoodList = request.dislikeFoodList().stream().map(DislikeFood::fromValue).toList();
        memberService.upsertPreference(dislikeFoodList);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/guided-spots", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postGuidedSpot(
            @Valid @RequestBody final GuidedSpotRequest request
    ) {
        memberService.createGuidedSpot(request.spotId());

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/saved-spots", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postSavedSpot(
            @Valid @RequestBody final SavedSpotRequest request
    ) {
        memberService.createSavedSpot(request.spotId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/saved-spots/{spotId}")
    public ResponseEntity<Void> deleteSavedSpot(
            @NotNull(message = "spotId는 필수입니다.")
            @Positive(message = "spotId는 양수여야 합니다.")
            @PathVariable(name = "spotId") final Long spotId
    ) {
        memberService.deleteSavedSpot(spotId);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(
                memberService.fetchProfile()
        );
    }

    @GetMapping(path = "/saved-spots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SavedSpotListResponse> getSavedSpotList() {
        return ResponseEntity.ok(
                memberService.fetchSavedSpotList()
        );
    }

    @PatchMapping(path = "/members/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patchProfile(
            @Valid @RequestBody ProfileRequest request
    ) {
        memberService.updateProfile(
                request.profileImage(),
                request.nickname(),
                request.birthDate()
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/images/presigned-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PreSignedUrlResponse> getPreSignedUrl(
            @NotBlank(message = "imageType은 공백일 수 없습니다.")
            @RequestParam(name = "imageType") final String imageTypeString
    ) {
        ImageType imageType = ImageType.fromValue(imageTypeString);

        return ResponseEntity.ok(
                memberService.fetchPreSignedUrl(imageType)
        );
    }

    @GetMapping(path = "/nickname/validate")
    public ResponseEntity<Void> getNicknameValidate(
            @NotBlank(message = "nickname은 공백일 수 없습니다.")
            @RequestParam(name = "nickname") final String nickname
    ) {
        memberService.validateNickname(nickname);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/verified-areas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifiedAreaListResponse> getVerifiedAreaList() {
        return ResponseEntity.ok(
                memberService.fetchVerifiedAreaList()
        );
    }

    @DeleteMapping(path = "/verified-areas/{verifiedAreaId}")
    public ResponseEntity<Void> deleteVerifiedArea(
            @NotNull(message = "verifiedAreaId는 필수입니다.")
            @Positive(message = "verifiedAreaId는 양수여야 합니다.")
            @PathVariable(name = "verifiedAreaId") final Long verifiedAreaId
    ) {
        memberService.deleteVerifiedArea(verifiedAreaId);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/verified-areas/replacement", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> replaceVerifiedArea(
            @Valid @RequestBody final ReplaceVerifiedAreaRequest request
    ) {
        memberService.replaceVerifiedArea(
                request.previousVerifiedAreaId(),
                request.latitude(),
                request.longitude()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/auth/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        memberService.logout(request.refreshToken());

        return ResponseEntity.ok().build();
    }

    @PostMapping(
            path = "/auth/reissue",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ReissueTokenResponse> reissueToken(
            @Valid @RequestBody ReissueTokenRequest request
    ) {
        return ResponseEntity.ok(
                memberService.reissueToken(request.refreshToken())
        );
    }

    @PostMapping(path = "/members/withdrawal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postWithdrawal(
            @Valid @RequestBody WithdrawalReasonRequest request
    ) {
        memberService.withdrawMember(request.reason(), request.refreshToken());

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/members/acorn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcornCountResponse> getAcornCount() {
        return ResponseEntity.ok(
                memberService.fetchAcornCount()
        );
    }
}
