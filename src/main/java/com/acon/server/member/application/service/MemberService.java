package com.acon.server.member.application.service;

import com.acon.server.global.auth.MemberAuthentication;
import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.global.auth.jwt.JwtTokenProvider;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.global.external.maps.NaverMapsAdapter;
import com.acon.server.global.external.s3.S3Adapter;
import com.acon.server.member.api.response.AcornCountResponse;
import com.acon.server.member.api.response.AppUpdateResponse;
import com.acon.server.member.api.response.LoginResponse;
import com.acon.server.member.api.response.PreSignedUrlResponse;
import com.acon.server.member.api.response.ProfileResponse;
import com.acon.server.member.api.response.ReissueTokenResponse;
import com.acon.server.member.api.response.SavedSpotListResponse;
import com.acon.server.member.api.response.SavedSpotResponse;
import com.acon.server.member.api.response.VerifiedAreaListResponse;
import com.acon.server.member.api.response.VerifiedAreaResponse;
import com.acon.server.member.application.mapper.GuidedSpotMapper;
import com.acon.server.member.application.mapper.MemberMapper;
import com.acon.server.member.domain.entity.GuidedSpot;
import com.acon.server.member.domain.entity.Member;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.domain.enums.ImageType;
import com.acon.server.member.domain.enums.Platform;
import com.acon.server.member.domain.enums.SocialType;
import com.acon.server.member.domain.vo.MemberIdentifiersVO;
import com.acon.server.member.infra.entity.GuidedSpotEntity;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.entity.PreferenceEntity;
import com.acon.server.member.infra.entity.SavedSpotEntity;
import com.acon.server.member.infra.entity.UpdatePolicyEntity;
import com.acon.server.member.infra.entity.VerifiedAreaEntity;
import com.acon.server.member.infra.entity.WithdrawalReasonEntity;
import com.acon.server.member.infra.external.google.GoogleSocialService;
import com.acon.server.member.infra.external.ios.AppleAuthAdapter;
import com.acon.server.member.infra.repository.GuidedSpotRepository;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.member.infra.repository.PreferenceRepository;
import com.acon.server.member.infra.repository.SavedSpotRepository;
import com.acon.server.member.infra.repository.UpdatePolicyRepository;
import com.acon.server.member.infra.repository.VerifiedAreaRepository;
import com.acon.server.member.infra.repository.WithdrawalReasonRepository;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.entity.SpotImageEntity;
import com.acon.server.spot.infra.repository.SpotImageRepository;
import com.acon.server.spot.infra.repository.SpotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private static final int MIN_NICKNAME_LENGTH = 1;
    private static final int MAX_NICKNAME_LENGTH = 14;
    private static final int MIN_VERIFIED_AREA_COUNT = 1;
    private static final int MAX_VERIFIED_AREA_COUNT = 3;
    private static final int DELETE_RESTRICTION_START_WEEK = 1;
    private static final int DELETE_RESTRICTION_END_MONTH = 3;
    private static final double MIN_LATITUDE = 33.1;
    private static final double MAX_LATITUDE = 38.6;
    private static final double MIN_LONGITUDE = 124.6;
    private static final double MAX_LONGITUDE = 131.9;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789_.".toCharArray();
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+(\\.\\d+){0,2}$");
    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^[a-z0-9_.]+$");
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final GuidedSpotRepository guidedSpotRepository;
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
    private final SavedSpotRepository savedSpotRepository;
    private final UpdatePolicyRepository updatePolicyRepository;
    private final VerifiedAreaRepository verifiedAreaRepository;
    private final WithdrawalReasonRepository withdrawalReasonRepository;

    private final SpotRepository spotRepository;
    private final SpotImageRepository spotImageRepository;

    private final GuidedSpotMapper guidedSpotMapper;
    private final MemberMapper memberMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalHandler principalHandler;

    // TODO: 네이밍 변경
    private final GoogleSocialService googleSocialService;
    private final AppleAuthAdapter appleAuthService;
    private final NaverMapsAdapter naverMapsAdapter;
    private final S3Adapter s3Adapter;

    @Value("${google.test-account-1}")
    private String testAccount1;

    @Value("${google.test-account-2}")
    private String testAccount2;

    @Value("${google.test-account-3}")
    private String testAccount3;

    @Value("${google.test-account-4}")
    private String testAccount4;

    @Transactional(readOnly = true)
    public AppUpdateResponse fetchForceUpdateRequired(
            final Platform platform,
            final String version
    ) {
        // TODO: 정규식 검증 추후 이동
//        if (!VERSION_PATTERN.matcher(version).matches()) {
//            throw new BusinessException(ErrorType.INVALID_VERSION_ERROR);
//        }

        List<UpdatePolicyEntity> updatePolicyEntityList = updatePolicyRepository.findAllByPlatformOrderById(platform);
        String normalizedVersion = normalize(version);

        boolean needForceUpdate = updatePolicyEntityList.stream()
                .anyMatch(p -> isBetween(
                        normalizedVersion,
                        p.getFromVersion(),
                        p.getToVersion()
                ));

        return new AppUpdateResponse(needForceUpdate);
    }

    private static String normalize(String version) {
        String[] p = version.split("\\.");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            sb.append(i < p.length ? Integer.parseInt(p[i]) : 0);

            if (i < 2) {
                sb.append('.');
            }
        }

        return sb.toString();
    }

    private static boolean isBetween(String target, String from, String to) {
        return compare(target, from) >= 0 && compare(target, to) <= 0;
    }

    private static int compare(String a, String b) {
        int[] v1 = parse(a);
        int[] v2 = parse(b);

        for (int i = 0; i < 3; i++) {
            int diff = v1[i] - v2[i];

            if (diff != 0) {
                return diff;
            }
        }

        return 0;
    }

    private static int[] parse(String v) {
        String[] p = v.split("\\.");

        return new int[]{
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                Integer.parseInt(p[2])
        };
    }

    // TODO: 메서드 순서 정리, TRANSACTION 설정, mapper 사용
    // TODO: @Valid 거친 건 원시타입으로 받기

    @Transactional
    public LoginResponse login(
            final SocialType socialType,
            final String idToken
    ) {
        String socialId;

        // TODO: 추후 전략 패턴 적용
        if (socialType == SocialType.GOOGLE) {
            socialId = googleSocialService.login(idToken);
        } else if (socialType == SocialType.APPLE) {
            socialId = appleAuthService.getAppleAccountId(idToken);
        } else {
            throw new BusinessException(ErrorType.INVALID_SOCIAL_TYPE_ERROR);
        }

        MemberIdentifiersVO memberIdsVO = fetchMemberIdAndExternalUUID(socialType, socialId);
        MemberAuthentication memberAuthentication = new MemberAuthentication(memberIdsVO.memberId(), null, null);
        String accessToken = jwtTokenProvider.issueAccessToken(memberAuthentication);
        String refreshToken = jwtTokenProvider.issueRefreshToken(memberIdsVO.memberId());

        boolean hasVerifiedArea = verifiedAreaRepository.existsByMemberId(memberIdsVO.memberId());

        // TODO: dto 파라미터 개수에 따른 컨벤션 고민 필요
        return LoginResponse.of(
                memberIdsVO.externalUUID(),
                accessToken,
                refreshToken,
                hasVerifiedArea
        );
    }

    private MemberIdentifiersVO fetchMemberIdAndExternalUUID(
            final SocialType socialType,
            final String socialId
    ) {

        MemberEntity member = memberRepository
                .findBySocialTypeAndSocialId(socialType, socialId)
                .orElseGet(() -> createMember(socialType, socialId));

        return MemberIdentifiersVO.of(member);
    }

    private MemberEntity createMember(
            final SocialType socialType,
            final String socialId
    ) {
        // TODO: 도메인 로직으로 이동할 부분들은 이동
        return memberRepository.save(
                MemberEntity.builder()
                        .socialType(socialType)
                        .socialId(socialId)
                        .externalUUID(generateUUID())
                        .profileImage(s3Adapter.getBasicProfileImageUrl())
                        .nickname(generateUniqueNickname())
                        .nicknameUpdatedAt(LocalDateTime.now())
                        .leftAcornCount(25)
                        .build()
        );
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private String generateUniqueNickname() {
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
    public void createVerifiedArea(
            final double latitude,
            final double longitude
    ) {
        if (isOutOfServiceArea(latitude, longitude)) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        long memberId = fetchMemberId();

        if (verifiedAreaRepository.countByMemberId(memberId) >= MAX_VERIFIED_AREA_COUNT) {
            throw new BusinessException(ErrorType.INVALID_VERIFIED_AREA_COUNT_ERROR);
        }

        String legalDong = naverMapsAdapter.getReverseGeoCodingResult(latitude, longitude);

        if (!verifiedAreaRepository.existsByMemberIdAndName(memberId, legalDong)) {
            createVerifiedArea(memberId, legalDong);
        }
    }

    // TODO: 순환 참조 방지를 위해 같은 메서드를 재선언했으므로, 추후 Facade 패턴을 통한 리팩토링 필요
    @Transactional(readOnly = true)
    public boolean checkTestUser() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());

        return memberEntity.getSocialId().equals(testAccount1) || memberEntity.getSocialId().equals(testAccount2)
                || memberEntity.getSocialId().equals(testAccount3) || memberEntity.getSocialId().equals(testAccount4);
    }

    // TODO: 공통 메서드로 빼기
    private boolean isOutOfServiceArea(
            final double latitude,
            final double longitude
    ) {
        return latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
                || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE;
    }

    // TODO: 공통 메서드로 빼기
    private long fetchMemberId() {
        long memberId = principalHandler.getMemberIdFromPrincipal();

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_MEMBER_ERROR);
        }

        return memberId;
    }

    private void createVerifiedArea(
            final long memberId,
            final String legalDong
    ) {
        verifiedAreaRepository.save(
                VerifiedAreaEntity.builder()
                        .memberId(memberId)
                        .name(legalDong)
                        .build()
        );
    }

    @Transactional
    public void upsertPreference(
            final List<DislikeFood> dislikeFoodList
    ) {
        long memberId = fetchMemberId();

        preferenceRepository.save(
                PreferenceEntity.builder()
                        .memberId(memberId)
                        .dislikeFoodList(dislikeFoodList)
                        .build()
        );
    }

    @Transactional
    public void createGuidedSpot(final long spotId) {
        if (principalHandler.isGuestUser()) { // TODO: 토글, 상세필터, 상세페이지, 길찾기 다 게스트 유저 접근 불가
            return;
        }

        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        long memberId = fetchMemberId();

        guidedSpotRepository.findByMemberIdAndSpotId(memberId, spotId)
                .ifPresentOrElse(
                        guidedSpotEntity -> {
                            GuidedSpot guidedSpot = guidedSpotMapper.toDomain(guidedSpotEntity);
                            guidedSpot.setUpdatedAtNow();

                            guidedSpotRepository.save(guidedSpotMapper.toEntity(guidedSpot));
                        },
                        () -> guidedSpotRepository.save(
                                GuidedSpotEntity.builder()
                                        .memberId(memberId)
                                        .spotId(spotId)
                                        .build()
                        )
                );
    }

    @Transactional
    public void createSavedSpot(final long spotId) {
        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        // TODO: memberId만 사용할 경우 아래처럼 리팩토링하기, 그리고 메서드로 빼기
        long memberId = fetchMemberId();

        if (savedSpotRepository.existsByMemberIdAndSpotId(memberId, spotId)) {
            return;
        }

        savedSpotRepository.save(
                SavedSpotEntity.builder()
                        .memberId(memberId)
                        .spotId(spotId)
                        .build()
        );
    }

    @Transactional
    public void deleteSavedSpot(final long spotId) {
        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        long memberId = fetchMemberId();
        savedSpotRepository.deleteByMemberIdAndSpotId(memberId, spotId);
    }

    @Transactional(readOnly = true)
    public ProfileResponse fetchProfile() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        List<SavedSpotEntity> savedSpotEntityList =
                savedSpotRepository.findTop10ByMemberIdOrderByIdDesc(memberEntity.getId());
        List<SavedSpotResponse> savedSpotResponseList = mapToSavedSpotResponseList(savedSpotEntityList);

        return ProfileResponse.builder()
                .profileImage(memberEntity.getProfileImage())
                .nickname(memberEntity.getNickname())
                .birthDate(
                        memberEntity.getBirthDate() != null
                                ? memberEntity.getBirthDate().format(BIRTH_DATE_FORMATTER)
                                : null
                )
                .savedSpotList(savedSpotResponseList)
                .build();
    }

    @Transactional(readOnly = true)
    public SavedSpotListResponse fetchSavedSpotList() {
        long memberId = fetchMemberId();
        List<SavedSpotEntity> savedSpotEntityList = savedSpotRepository.findAllByMemberIdOrderByIdDesc(memberId);
        List<SavedSpotResponse> savedSpotResponseList = mapToSavedSpotResponseList(savedSpotEntityList);

        return SavedSpotListResponse.of(savedSpotResponseList);
    }

    private List<SavedSpotResponse> mapToSavedSpotResponseList(final List<SavedSpotEntity> savedSpotEntityList) {
        List<Long> spotIdList = savedSpotEntityList.stream()
                .map(SavedSpotEntity::getSpotId)
                .toList();

        Map<Long, SpotEntity> spotMap = spotRepository.findAllById(spotIdList).stream()
                .collect(
                        Collectors.toMap(
                                SpotEntity::getId,
                                Function.identity(),
                                (existing, replacement) -> existing,
                                LinkedHashMap::new // 순서 보장
                        )
                );

        Map<Long, String> spotImageMap = spotImageRepository.findMainImagesBySpotIds(spotIdList).stream()
                .collect(
                        Collectors.toMap(
                                SpotImageEntity::getSpotId,
                                SpotImageEntity::getImage,
                                (existing, replacement) -> existing
                        )
                );

        return savedSpotEntityList.stream()
                .map(
                        savedSpotEntity -> {
                            SpotEntity spotEntity = spotMap.get(savedSpotEntity.getSpotId());

                            if (spotEntity == null) {
                                return null;
                            }

                            String image = spotImageMap.get(savedSpotEntity.getSpotId());

                            return SavedSpotResponse.builder()
                                    .spotId(spotEntity.getId())
                                    .image(image)
                                    .name(spotEntity.getName())
                                    .build();
                        }
                )
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public void updateProfile(
            final String profileImage,
            final String nickname,
            final String birthDate
    ) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        Member member = memberMapper.toDomain(memberEntity);

        if (!member.getProfileImage().equals(profileImage)) {
            if (profileImage == null || profileImage.isEmpty()) {
                String basicProfileImageUrl = s3Adapter.getBasicProfileImageUrl();
                member.setProfileImage(basicProfileImageUrl);
            } else {
                s3Adapter.validateProfileImageExists(profileImage);
                String imageUrl = s3Adapter.getProfileImageUrl(profileImage);
                // TODO: 추후 원자성(2PC) 보장을 위해 트랜잭션 커밋된 이후 이벤트/트리거 방식으로 비동기 처리
                s3Adapter.deleteFile(member.getProfileImage());
                member.setProfileImage(imageUrl);
            }
        }

        if (!member.getNickname().equals(nickname)) {
            validateNickname(nickname);
            member.setNickname(nickname);
        }

        if (birthDate == null || birthDate.isEmpty()) {
            member.setBirthDate(null);
        } else {
            LocalDate currentBirthDate = member.getBirthDate();

            if (currentBirthDate == null || !birthDate.equals(currentBirthDate.toString())) {
                LocalDate parsedBirthDate = validateAndParseBirthDate(birthDate);
                member.setBirthDate(parsedBirthDate);
            }
        }

        memberRepository.save(
                memberMapper.toEntity(member)
        );
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
        if (!NICKNAME_PATTERN.matcher(nickname).matches()) {
            throw new BusinessException(ErrorType.INVALID_NICKNAME_ERROR);
        }
    }

    private void validateNicknameLength(final String nickname) {
        if (nickname.length() < MIN_NICKNAME_LENGTH || nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new BusinessException(ErrorType.INVALID_NICKNAME_ERROR);
        }
    }

    private void validateNicknameDuplication(final String nickname) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());

        if (memberEntity.getNickname().equals(nickname)) {
            return;
        }

        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(ErrorType.DUPLICATE_NICKNAME_ERROR);
        }
    }

    private LocalDate validateAndParseBirthDate(final String birthDate) {
        try {
            LocalDate parsedDate = LocalDate.parse(
                    birthDate,
                    BIRTH_DATE_FORMATTER.withResolverStyle(ResolverStyle.SMART)
            );

            if (parsedDate.isAfter(LocalDate.now())) {
                throw new BusinessException(ErrorType.INVALID_BIRTH_DATE_ERROR);
            }

            return parsedDate;
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorType.INVALID_BIRTH_DATE_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public VerifiedAreaListResponse fetchVerifiedAreaList() {
        long memberId = fetchMemberId();

        List<VerifiedAreaEntity> verifiedAreaEntityList =
                verifiedAreaRepository.findAllByMemberIdOrderById(memberId);
        List<VerifiedAreaResponse> verifiedAreaList = verifiedAreaEntityList.stream()
                .map(VerifiedAreaResponse::of)
                .toList();

        return VerifiedAreaListResponse.of(verifiedAreaList);
    }

    @Transactional
    public void deleteVerifiedArea(final long verifiedAreaId) {
        long memberId = fetchMemberId();
        VerifiedAreaEntity verifiedAreaEntity = verifiedAreaRepository.findByIdOrElseThrow(verifiedAreaId);

        if (!verifiedAreaEntity.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorType.INVALID_VERIFIED_AREA_ERROR);
        }

        validateVerifiedAreaDeleteRestriction(verifiedAreaEntity.getCreatedAt());

        if (verifiedAreaRepository.countByMemberId(memberId) <= MIN_VERIFIED_AREA_COUNT) {
            throw new BusinessException(ErrorType.INVALID_VERIFIED_AREA_COUNT_ERROR);
        }

        verifiedAreaRepository.deleteById(verifiedAreaId);
    }

    private void validateVerifiedAreaDeleteRestriction(final LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAfter = createdAt.plusWeeks(DELETE_RESTRICTION_START_WEEK);
        LocalDateTime threeMonthsAfter = createdAt.plusMonths(DELETE_RESTRICTION_END_MONTH);

        if (now.isAfter(oneWeekAfter) && now.isBefore(threeMonthsAfter)) {
            throw new BusinessException(ErrorType.VERIFIED_AREA_DELETE_RESTRICTION_ERROR);
        }
    }

    @Transactional
    public void replaceVerifiedArea(
            final long verifiedAreaId,
            final double latitude,
            final double longitude
    ) {
        if (isOutOfServiceArea(latitude, longitude)) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        long memberId = fetchMemberId();
        VerifiedAreaEntity verifiedAreaEntity = verifiedAreaRepository.findByIdOrElseThrow(verifiedAreaId);

        if (!verifiedAreaEntity.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorType.INVALID_VERIFIED_AREA_ERROR);
        }

        validateVerifiedAreaDeleteRestriction(verifiedAreaEntity.getCreatedAt());

        if (verifiedAreaRepository.countByMemberId(memberId) != MIN_VERIFIED_AREA_COUNT) {
            throw new BusinessException(ErrorType.VERIFIED_AREA_REPLACE_RESTRICTION_ERROR);
        }

        String legalDong = naverMapsAdapter.getReverseGeoCodingResult(latitude, longitude);

        if (legalDong.equals(verifiedAreaEntity.getName())) {
            return;
        }

        if (!verifiedAreaRepository.existsByMemberIdAndName(memberId, legalDong)) {
            verifiedAreaRepository.deleteById(verifiedAreaId);
            createVerifiedArea(memberId, legalDong);
        }
    }

    @Transactional
    public void logout(final String refreshToken) {
        long memberId = fetchMemberId();

        if (!jwtTokenProvider.validateRefreshToken(refreshToken).equals(memberId)) {
            throw new BusinessException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
        }

        jwtTokenProvider.deleteRefreshToken(refreshToken);
    }

    @Transactional
    public ReissueTokenResponse reissueToken(final String refreshToken) {
        // TODO: 리팩토링
        Long memberId = jwtTokenProvider.validateRefreshToken(refreshToken);

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_MEMBER_ERROR);
        }

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
        long memberId = fetchMemberId();

        // TODO: memberId 존재하는 테이블에 member row 제거 ( 리뷰 테이블 제외 )
        memberRepository.deleteById(memberId);
        jwtTokenProvider.deleteRefreshToken(refreshToken);
        // TODO: 엑세스 토큰 블랙리스트

        withdrawalReasonRepository.save(
                WithdrawalReasonEntity.builder()
                        .reason(reason)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public AcornCountResponse fetchAcornCount() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        int acornCount = memberEntity.getLeftAcornCount();

        return AcornCountResponse.of(acornCount);
    }

    // TODO: 최근 길 안내 장소 지우는 스케줄러 추가
}
