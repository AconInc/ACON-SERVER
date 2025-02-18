package com.acon.server.member.application.service;

import com.acon.server.global.auth.MemberAuthentication;
import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.global.auth.jwt.JwtTokenProvider;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.global.external.maps.NaverMapsAdapter;
import com.acon.server.global.external.s3.S3Adapter;
import com.acon.server.member.api.response.AcornCountResponse;
import com.acon.server.member.api.response.LoginResponse;
import com.acon.server.member.api.response.PreSignedUrlResponse;
import com.acon.server.member.api.response.ProfileResponse;
import com.acon.server.member.api.response.ReissueTokenResponse;
import com.acon.server.member.api.response.VerifiedAreaListResponse;
import com.acon.server.member.api.response.VerifiedAreaResponse;
import com.acon.server.member.application.mapper.GuidedSpotMapper;
import com.acon.server.member.application.mapper.MemberMapper;
import com.acon.server.member.application.mapper.PreferenceMapper;
import com.acon.server.member.application.mapper.VerifiedAreaMapper;
import com.acon.server.member.domain.entity.GuidedSpot;
import com.acon.server.member.domain.entity.Member;
import com.acon.server.member.domain.entity.Preference;
import com.acon.server.member.domain.entity.VerifiedArea;
import com.acon.server.member.domain.enums.Cuisine;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.domain.enums.FavoriteSpot;
import com.acon.server.member.domain.enums.ImageType;
import com.acon.server.member.domain.enums.SocialType;
import com.acon.server.member.domain.enums.SpotStyle;
import com.acon.server.member.infra.entity.GuidedSpotEntity;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.entity.VerifiedAreaEntity;
import com.acon.server.member.infra.entity.WithdrawalReasonEntity;
import com.acon.server.member.infra.external.google.GoogleSocialService;
import com.acon.server.member.infra.external.ios.AppleAuthAdapter;
import com.acon.server.member.infra.repository.GuidedSpotRepository;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.member.infra.repository.PreferenceRepository;
import com.acon.server.member.infra.repository.VerifiedAreaRepository;
import com.acon.server.member.infra.repository.WithdrawalReasonRepository;
import com.acon.server.spot.domain.enums.SpotType;
import com.acon.server.spot.infra.repository.SpotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final char[] CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.".toCharArray();
    private static final int MAX_NICKNAME_LENGTH = 16;
    private static final String NICKNAME_PATTERN = "^[a-zA-Z0-9_.가-힣]+$";
    private static final int MIN_VERIFIED_AREA_SIZE = 1;
    private static final int MAX_VERIFIED_AREA_SIZE = 5;

    private final GuidedSpotRepository guidedSpotRepository;
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final VerifiedAreaRepository verifiedAreaRepository;
    private final SpotRepository spotRepository;
    private final WithdrawalReasonRepository withdrawalReasonRepository;

    private final GuidedSpotMapper guidedSpotMapper;
    private final MemberMapper memberMapper;
    private final PreferenceMapper preferenceMapper;
    private final VerifiedAreaMapper verifiedAreaMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalHandler principalHandler;

    // TODO: 네이밍 변경
    private final GoogleSocialService googleSocialService;
    private final AppleAuthAdapter appleAuthService;

    private final NaverMapsAdapter naverMapsAdapter;

    private final S3Adapter s3Adapter;

    // TODO: 메서드 순서 정리, TRANSACTION 설정, mapper 사용
    // TODO: @Valid 거친 건 원시타입으로 받기

    @Transactional
    public LoginResponse login(
            final SocialType socialType,
            final String idToken
    ) {
        String socialId;

        if (socialType == SocialType.GOOGLE) {
            socialId = googleSocialService.login(idToken);
        } else if (socialType == SocialType.APPLE) {
            socialId = appleAuthService.getAppleAccountId(idToken);
        } else {
            throw new BusinessException(ErrorType.INVALID_SOCIAL_TYPE_ERROR);
        }

        Long memberId = fetchMemberId(socialType, socialId);
        MemberAuthentication memberAuthentication = new MemberAuthentication(memberId, null, null);
        String accessToken = jwtTokenProvider.issueAccessToken(memberAuthentication);
        String refreshToken = jwtTokenProvider.issueRefreshToken(memberId);

        boolean hasVerifiedArea = verifiedAreaRepository.existsByMemberId(memberId);

        return LoginResponse.of(accessToken, refreshToken, hasVerifiedArea);
    }

    private Long fetchMemberId(
            final SocialType socialType,
            final String socialId
    ) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findBySocialTypeAndSocialId(socialType,
                socialId);
        MemberEntity memberEntity = optionalMemberEntity.orElseGet(() ->
                memberRepository.save(MemberEntity.builder()
                        .socialType(socialType)
                        .socialId(socialId)
                        .leftAcornCount(25)
                        .nickname(generateUniqueNickname())
                        .profileImage(s3Adapter.getBasicProfileImageUrl())
                        .build())
        );

        Member member = memberMapper.toDomain(memberEntity);

        return member.getId();
    }

    public String generateUniqueNickname() {
        String nickname;
        do {
            nickname = generateRandomNickname();
        } while (memberRepository.existsByNickname(nickname));

        return nickname;
    }

    private String generateRandomNickname() {
        char[] nickname = new char[MAX_NICKNAME_LENGTH];
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < MAX_NICKNAME_LENGTH; i++) {
            nickname[i] = CHARACTERS[random.nextInt(CHARACTERS.length)];
        }

        return new String(nickname);
    }

    @Transactional
    public VerifiedAreaResponse createVerifiedArea(
            final Double latitude,
            final Double longitude
    ) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

        if (verifiedAreaRepository.countByMemberId(memberEntity.getId()) >= MAX_VERIFIED_AREA_SIZE) {
            throw new BusinessException(ErrorType.INVALID_AREA_SIZE_ERROR);
        }

        String legalDong = naverMapsAdapter.getReverseGeoCodingResult(latitude, longitude);
        Optional<VerifiedAreaEntity> optionalVerifiedAreaEntity = verifiedAreaRepository.findByMemberIdAndName(
                memberEntity.getId(), legalDong);

        LocalDate currentDate = LocalDate.now();
        VerifiedAreaEntity savedVerifiedAreaEntity = optionalVerifiedAreaEntity
                .map(entity -> updateVerifiedAreaEntity(entity, currentDate))
                .orElseGet(() -> createVerifiedAreaEntity(legalDong, memberEntity.getId(), currentDate));

        return VerifiedAreaResponse.of(savedVerifiedAreaEntity.getId(), savedVerifiedAreaEntity.getName());
    }

    @Transactional(readOnly = true)
    public VerifiedAreaListResponse fetchVerifiedAreaList() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        List<VerifiedAreaEntity> verifiedAreaEntityList = verifiedAreaRepository.findAllByMemberId(
                memberEntity.getId());
        List<VerifiedAreaResponse> verifiedAreaList = verifiedAreaEntityList.stream()
                .map(verifiedAreaEntity -> VerifiedAreaResponse.of(verifiedAreaEntity.getId(),
                        verifiedAreaEntity.getName()))
                .toList();

        return new VerifiedAreaListResponse(verifiedAreaList);
    }

    @Transactional
    public void deleteVerifiedArea(final Long verifiedAreaId) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        VerifiedAreaEntity verifiedAreaEntity = verifiedAreaRepository.findByIdOrElseThrow(verifiedAreaId);

        if (!verifiedAreaEntity.getMemberId().equals(memberEntity.getId())) {
            throw new BusinessException(ErrorType.INVALID_VERIFIED_AREA_ERROR);
        }

        if (verifiedAreaRepository.countByMemberId(memberEntity.getId()) <= MIN_VERIFIED_AREA_SIZE) {
            throw new BusinessException(ErrorType.INVALID_AREA_SIZE_ERROR);
        }

        verifiedAreaRepository.deleteById(verifiedAreaId);
    }

    private VerifiedAreaEntity updateVerifiedAreaEntity(
            final VerifiedAreaEntity entity,
            final LocalDate currentDate
    ) {
        VerifiedArea verifiedArea = verifiedAreaMapper.toDomain(entity);
        verifiedArea.updateVerifiedDate(currentDate);
        return verifiedAreaRepository.save(verifiedAreaMapper.toEntity(verifiedArea));
    }

    private VerifiedAreaEntity createVerifiedAreaEntity(
            final String legalDong,
            final Long memberId,
            final LocalDate currentDate
    ) {
        return verifiedAreaRepository.save(
                VerifiedAreaEntity.builder()
                        .name(legalDong)
                        .memberId(memberId)
                        .verifiedDate(Collections.singletonList(currentDate))
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public String fetchMemberArea(
            final Double latitude,
            final Double longitude
    ) {
        return naverMapsAdapter.getReverseGeoCodingResult(latitude, longitude);
    }

    @Transactional
    public void upsertPreference(
            final List<DislikeFood> dislikeFoodList,
            final List<Cuisine> favoriteCuisineList,
            final SpotType favoriteSpotType,
            final SpotStyle favoriteSpotStyle,
            final List<FavoriteSpot> favoriteSpotRank
    ) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

        Preference preference = Preference.builder()
                .memberId(memberEntity.getId())
                .dislikeFoodList(dislikeFoodList)
                .favoriteCuisineRank(favoriteCuisineList)
                .favoriteSpotType(favoriteSpotType)
                .favoriteSpotStyle(favoriteSpotStyle)
                .favoriteSpotRank(favoriteSpotRank)
                .build();

        preferenceRepository.save(preferenceMapper.toEntity(preference));
    }

    @Transactional
    public void createGuidedSpot(final Long spotId) {
        if (principalHandler.isGuestUser()) {
            return;
        }

        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

        Optional<GuidedSpotEntity> optionalGuidedSpotEntity =
                guidedSpotRepository.findByMemberIdAndSpotId(memberEntity.getId(), spotId);

        optionalGuidedSpotEntity.ifPresentOrElse(
                guidedSpotEntity -> {
                    GuidedSpot guidedSpot = guidedSpotMapper.toDomain(guidedSpotEntity);
                    guidedSpot.updateUpdatedAt(LocalDateTime.now());
                    guidedSpotRepository.save(guidedSpotMapper.toEntity(guidedSpot));
                },
                () -> guidedSpotRepository.save(
                        GuidedSpotEntity.builder()
                                .memberId(memberEntity.getId())
                                .spotId(spotId)
                                .build()
                )
        );
    }

    @Transactional(readOnly = true)
    public AcornCountResponse fetchAcornCount() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        int acornCount = memberEntity.getLeftAcornCount();

        return new AcornCountResponse(acornCount);
    }

    @Transactional(readOnly = true)
    public ProfileResponse fetchProfile() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        List<VerifiedAreaEntity> verifiedAreaEntityList = verifiedAreaRepository.findAllByMemberId(
                memberEntity.getId());

        return ProfileResponse.builder().
                image(memberEntity.getProfileImage())
                .nickname(memberEntity.getNickname())
                .birthDate(memberEntity.getBirthDate() != null ? memberEntity.getBirthDate().toString() : null)
                .leftAcornCount(memberEntity.getLeftAcornCount())
                .verifiedAreaList(verifiedAreaEntityList.stream()
                        .map(verifiedAreaEntity -> new ProfileResponse.VerifiedArea(verifiedAreaEntity.getId(),
                                verifiedAreaEntity.getName()))
                        .toList())
                .build();
    }

    public PreSignedUrlResponse fetchPreSignedUrl(final ImageType imageType) {
        // TODO: 확장자 방식 고민하기
        String fileName = UUID.randomUUID() + ".jpg";

        String preSignedUrl = switch (imageType) {
            case PROFILE -> s3Adapter.getPreSignedUrlForProfileImage(fileName);
//            case REVIEW -> s3Adapter.getPreSignedUrlForReviewImage(fileName);
//            case SPOT -> s3Adapter.getPreSignedUrlForSpotImage(fileName);
            default -> throw new BusinessException(ErrorType.INVALID_IMAGE_TYPE_ERROR);
        };

        return PreSignedUrlResponse.of(fileName, preSignedUrl);
    }

    @Transactional(readOnly = true)
    public void validateNickname(final String nickname) {
        validateNicknamePattern(nickname);
        validateNicknameLength(nickname);
        validateNicknameDuplication(nickname);
    }

    private void validateNicknamePattern(final String nickname) {
        if (!nickname.matches(NICKNAME_PATTERN)) {
            throw new BusinessException(ErrorType.INVALID_NICKNAME_ERROR);
        }
    }

    private void validateNicknameLength(final String nickname) {
        int length = calculateNicknameLength(nickname);

        if (length < 1 || length > MAX_NICKNAME_LENGTH) {
            throw new BusinessException(ErrorType.INVALID_NICKNAME_ERROR);
        }
    }

    private int calculateNicknameLength(final String nickname) {
        int length = 0;

        for (char c : nickname.toCharArray()) {
            if (isKorean(c)) {
                length += 2;
            } else {
                length += 1;
            }
        }

        return length;
    }

    private boolean isKorean(char c) {
        return (c >= 0xAC00 && c <= 0xD7A3);
    }

    private void validateNicknameDuplication(final String nickname) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

        if (memberEntity.getNickname().equals(nickname)) {
            return;
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorType.DUPLICATED_NICKNAME_ERROR);
        }
    }

    @Transactional
    public void updateProfile(
            final String profileImage,
            final String nickname,
            final String birthDate
    ) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        Member member = memberMapper.toDomain(memberEntity);

        if (!profileImage.equals(member.getProfileImage())) {
            if (profileImage.isEmpty()) {
                String basicProfileImageUrl = s3Adapter.getBasicProfileImageUrl();
                member.setProfileImage(basicProfileImageUrl);
            } else {
                s3Adapter.validateProfileImageExists(profileImage);
                String imageUrl = s3Adapter.getProfileImageUrl(profileImage);
                s3Adapter.deleteFile(member.getProfileImage());
                member.setProfileImage(imageUrl);
            }
        }

        if (!nickname.equals(member.getNickname())) {
            validateNickname(nickname);
            member.setNickname(nickname);
        }

        if (birthDate == null) {
            member.setBirthDate(null);
        } else if (member.getBirthDate() == null || !birthDate.equals(member.getBirthDate().toString())) {
            LocalDate parsedBirthDate = validateAndParseBirthDate(birthDate);
            member.setBirthDate(parsedBirthDate);
        }

        memberRepository.save(memberMapper.toEntity(member));
    }

    private LocalDate validateAndParseBirthDate(final String birthDate) {
        try {
            LocalDate parsedDate = LocalDate.parse(
                    birthDate,
                    DateTimeFormatter.ofPattern("yyyy.MM.dd").withResolverStyle(ResolverStyle.SMART)
            );

            if (parsedDate.isAfter(LocalDate.now())) {
                throw new BusinessException(ErrorType.INVALID_BIRTH_DATE_ERROR);
            }

            return parsedDate;
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorType.INVALID_BIRTH_DATE_ERROR);
        }
    }

    @Transactional
    public void logout(final String refreshToken) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        if (!memberEntity.getId().equals(jwtTokenProvider.validateRefreshToken(refreshToken))) {
            throw new BusinessException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
        }
        jwtTokenProvider.deleteRefreshToken(refreshToken);
    }

    @Transactional
    public ReissueTokenResponse reissueToken(final String refreshToken) {
        // TODO: 리팩토링
        Long memberId = jwtTokenProvider.validateRefreshToken(refreshToken);
        memberRepository.findByIdOrElseThrow(memberId);

        jwtTokenProvider.deleteRefreshToken(refreshToken);

        MemberAuthentication memberAuthentication = new MemberAuthentication(memberId, null, null);
        String newAccessToken = jwtTokenProvider.issueAccessToken(memberAuthentication);
        String newRefreshToken = jwtTokenProvider.issueRefreshToken(memberId);
        return ReissueTokenResponse.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void withdrawMember(
            final String reason,
            final String refreshToken
    ) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

        // TODO: memberId 존재하는 테이블에 member row 제거 ( 리뷰 테이블 제외 )
        memberRepository.deleteById(memberEntity.getId());
        jwtTokenProvider.deleteRefreshToken(refreshToken);
        // TODO: 엑세스 토큰 블랙리스트

        withdrawalReasonRepository.save(
                WithdrawalReasonEntity.builder()
                        .reason(reason)
                        .build()
        );
    }

    // TODO: 최근 길 안내 장소 지우는 스케줄러 추가
}