package com.acon.server.member.api.controller;

import com.acon.server.member.api.request.GuidedSpotRequest;
import com.acon.server.member.api.request.LoginRequest;
import com.acon.server.member.api.request.LogoutRequest;
import com.acon.server.member.api.request.PreferenceRequest;
import com.acon.server.member.api.request.ProfileRequest;
import com.acon.server.member.api.request.ReissueTokenRequest;
import com.acon.server.member.api.request.VerifiedAreaRequest;
import com.acon.server.member.api.request.WithdrawalReasonRequest;
import com.acon.server.member.api.response.AcornCountResponse;
import com.acon.server.member.api.response.AreaResponse;
import com.acon.server.member.api.response.LoginResponse;
import com.acon.server.member.api.response.PreSignedUrlResponse;
import com.acon.server.member.api.response.ProfileResponse;
import com.acon.server.member.api.response.ReissueTokenResponse;
import com.acon.server.member.api.response.VerifiedAreaListResponse;
import com.acon.server.member.api.response.VerifiedAreaResponse;
import com.acon.server.member.application.service.MemberService;
import com.acon.server.member.domain.enums.Cuisine;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.domain.enums.FavoriteSpot;
import com.acon.server.member.domain.enums.ImageType;
import com.acon.server.member.domain.enums.SocialType;
import com.acon.server.member.domain.enums.SpotStyle;
import com.acon.server.spot.domain.enums.SpotType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class MemberController {

    private final MemberService memberService;

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

    @PostMapping(path = "/members/verified-areas",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<VerifiedAreaResponse> postVerifiedArea(
            @Valid @RequestBody final VerifiedAreaRequest request
    ) {
        if (memberService.checkTestUser()) {
            return ResponseEntity.ok(
                    memberService.createVerifiedArea(37.559115, 126.921976)
            );
        }

        return ResponseEntity.ok(
                memberService.createVerifiedArea(request.latitude(), request.longitude())
        );
    }

    @GetMapping(path = "/members/verified-areas", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VerifiedAreaListResponse> getVerifiedAreaList(
    ) {
        return ResponseEntity.ok(
                memberService.fetchVerifiedAreaList()
        );
    }

    @DeleteMapping(path = "/members/verified-areas/{verifiedAreaId}")
    public ResponseEntity<Void> deleteVerifiedArea(
            @Positive(message = "verifiedAreaId는 양수여야 합니다.")
            @PathVariable(name = "verifiedAreaId") final Long verifiedAreaId
    ) {
        memberService.deleteVerifiedArea(verifiedAreaId);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/area", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AreaResponse> getArea(
            @NotNull(message = "위도는 필수입니다.")
            @Validated @RequestParam(name = "latitude") final Double latitude,
            @NotNull(message = "경도는 필수입니다.")
            @Validated @RequestParam(name = "longitude") final Double longitude
    ) {
        if (memberService.checkTestUser()) {
            return ResponseEntity.ok(
                    memberService.fetchMemberArea(37.559115, 126.921976)
            );
        }

        return ResponseEntity.ok(
                memberService.fetchMemberArea(latitude, longitude)
        );
    }

    @PutMapping(path = "/members/preference", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> putPreference(
            @Valid @RequestBody final PreferenceRequest request
    ) {
        List<DislikeFood> dislikeFoodList = request.dislikeFoodList().stream().map(DislikeFood::fromValue).toList();
        List<Cuisine> favoriteCuisineList = request.favoriteCuisineRank().stream().map(Cuisine::fromValue).toList();
        SpotType favoriteSpotType = SpotType.fromValue(request.favoriteSpotType());
        SpotStyle favoriteSpotStyle = SpotStyle.fromValue(request.favoriteSpotStyle());
        List<FavoriteSpot> favoriteSpotRank = request.favoriteSpotRank().stream().map(FavoriteSpot::fromValue).toList();

        memberService.upsertPreference(dislikeFoodList, favoriteCuisineList, favoriteSpotType, favoriteSpotStyle,
                favoriteSpotRank);

        return ResponseEntity.ok().build();
    }

    // TODO: Member 도메인에 있어야 할까? 고민 필요
    @PostMapping(path = "/members/guided-spots", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> postGuidedSpot(
            @Valid @RequestBody final GuidedSpotRequest request
    ) {
        memberService.createGuidedSpot(request.spotId());

        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/members/acorn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AcornCountResponse> getAcornCount() {
        return ResponseEntity.ok(
                memberService.fetchAcornCount()
        );
    }

    @GetMapping(path = "/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(
                memberService.fetchProfile()
        );
    }

    @GetMapping(path = "/images/presigned-url", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PreSignedUrlResponse> getPreSignedUrl(
            @RequestParam(name = "imageType") final String imageTypeString
    ) {
        ImageType imageType = ImageType.fromValue(imageTypeString);

        return ResponseEntity.ok(
                memberService.fetchPreSignedUrl(imageType)
        );
    }

    @GetMapping(path = "/members/nickname/validate")
    public ResponseEntity<Void> getNicknameValidate(
            @RequestParam(name = "nickname") final String nickname
    ) {
        memberService.validateNickname(nickname);

        return ResponseEntity.ok().build();
    }

    @PatchMapping(path = "/members/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> patchProfile(
            @Valid @RequestBody ProfileRequest request
    ) {
        memberService.updateProfile(request.profileImage(), request.nickname(), request.birthDate());

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/auth/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        memberService.logout(request.refreshToken());

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/auth/reissue",
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
}
