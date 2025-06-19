package com.acon.server.spot.application.service;

import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.global.external.maps.GeoCodingResponse;
import com.acon.server.global.external.maps.NaverMapsAdapter;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.entity.PreferenceEntity;
import com.acon.server.member.infra.repository.GuidedSpotCustomRepository;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.member.infra.repository.PreferenceRepository;
import com.acon.server.member.infra.repository.SavedSpotRepository;
import com.acon.server.review.infra.repository.ReviewRepository;
import com.acon.server.spot.api.request.SpotListRequest;
import com.acon.server.spot.api.response.MenuResponse;
import com.acon.server.spot.api.response.MenuboardImageListResponse;
import com.acon.server.spot.api.response.SearchSpotListResponse;
import com.acon.server.spot.api.response.SearchSpotResponse;
import com.acon.server.spot.api.response.SearchSuggestionListResponse;
import com.acon.server.spot.api.response.SearchSuggestionResponse;
import com.acon.server.spot.api.response.SpotDetailResponse;
import com.acon.server.spot.api.response.SpotListResponse;
import com.acon.server.spot.api.response.SpotListResponse.RecommendedSpot;
import com.acon.server.spot.application.mapper.SpotMapper;
import com.acon.server.spot.domain.entity.Spot;
import com.acon.server.spot.domain.enums.SpotType;
import com.acon.server.spot.domain.enums.Tag;
import com.acon.server.spot.infra.entity.MenuEntity;
import com.acon.server.spot.infra.entity.MenuboardImageEntity;
import com.acon.server.spot.infra.entity.OpeningHourEntity;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.entity.SpotImageEntity;
import com.acon.server.spot.infra.repository.MenuRepository;
import com.acon.server.spot.infra.repository.MenuboardImageRepository;
import com.acon.server.spot.infra.repository.OpeningHourRepository;
import com.acon.server.spot.infra.repository.SpotImageRepository;
import com.acon.server.spot.infra.repository.SpotNativeQueryRepository;
import com.acon.server.spot.infra.repository.SpotOptionRepository;
import com.acon.server.spot.infra.repository.SpotRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotService {

    private final PreferenceRepository preferenceRepository;

    // TODO: 매직 넘버 yml로 옮기기
    private static final double WALKING_RADIUS = 1000.0; // 시속 3km
    private static final double BIKING_RADIUS = 4000.0; // 시속 12km
    private static final int SUGGESTION_RADIUS = 250;
    private static final int SUGGESTION_LIMIT = 5;
    private static final int VERIFICATION_DISTANCE = 250;
    private static final int SEARCH_LIMIT = 10;
    private static final double MIN_LATITUDE = 33.1;
    private static final double MAX_LATITUDE = 38.6;
    private static final double MIN_LONGITUDE = 124.6;
    private static final double MAX_LONGITUDE = 131.9;

    private final GuidedSpotCustomRepository guidedSpotCustomRepository;
    private final MemberRepository memberRepository;

    private final MenuboardImageRepository menuboardImageRepository;
    private final MenuRepository menuRepository;
    private final OpeningHourRepository openingHourRepository;
    private final SpotImageRepository spotImageRepository;
    private final SpotNativeQueryRepository spotNativeQueryRepository;
    private final SpotOptionRepository spotOptionRepository;
    private final SpotRepository spotRepository;

    private final SpotMapper spotMapper;

    private final PrincipalHandler principalHandler;

    private final NaverMapsAdapter naverMapsAdapter;
    private final ReviewRepository reviewRepository;
    private final SavedSpotRepository savedSpotRepository;

    @Value("${google.test-account-1}")
    private String testAccount1;

    @Value("${google.test-account-2}")
    private String testAccount2;

    @Value("${google.test-account-3}")
    private String testAccount3;

    @Value("${google.test-account-4}")
    private String testAccount4;

    // 메서드 설명: 위치 정보가 없는 Spot들의 위치 정보를 업데이트한다.
    @Transactional
    public void updateNullCoordinatesForSpots() {
        List<SpotEntity> spotEntityList = spotRepository.findAllByLatitudeIsNullOrLongitudeIsNullOrGeomIsNullOrLegalDongIsNull();

        if (spotEntityList.isEmpty()) {
            log.info("위치 정보가 비어 있는 Spot 데이터가 없습니다.");

            return;
        }

        log.info("위치 정보가 비어 있는 Spot 데이터를 {}건 찾았습니다.", spotEntityList.size());
        List<SpotEntity> updatedEntityList = spotEntityList.stream()
                .map(
                        spotEntity -> {
                            Spot spot = spotMapper.toDomain(spotEntity);
                            updateSpotCoordinate(spot);

                            return spotMapper.toEntity(spot);
                        }
                )
                .toList();

        spotRepository.saveAll(updatedEntityList);
        log.info("위치 정보가 비어 있는 Spot 데이터 {}건을 업데이트 했습니다.", updatedEntityList.size());
    }

    // TODO: 로직 정리
    // 메서드 설명: 도로명 주소를 바탕으로 spotId에 해당하는 Spot의 위치 정보를 업데이트한다.
    private void updateSpotCoordinate(final Spot spot) {
        GeoCodingResponse geoCodingResponse = naverMapsAdapter.getGeoCodingResult(spot.getAddress());
        spot.updateCoordinate(
                Double.parseDouble(geoCodingResponse.latitude()),
                Double.parseDouble(geoCodingResponse.longitude())
        );
        spot.updateGeom();
        spot.updateLegalDong(naverMapsAdapter.getReverseGeoCodingResult(spot.getLatitude(), spot.getLongitude()));
        spotRepository.initCreatedAtIfNull(spot.getId());
    }

    // TODO: 전체 파라미터 타입 확인
    // TODO: 정렬 기준 적용
    // TODO: 싫어하는 음식 필터 적용
    // TODO: 변수명 정리

    @Transactional(readOnly = true)
    public SpotListResponse fetchRecommendedSpotList(final SpotListRequest request) {
        if (isOutOfServiceArea(request.latitude(), request.longitude())) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        // TODO: 토글, 상세필터, 상세페이지, 길찾기 다 게스트 유저 접근 불가
        // TODO: 장소는 최대 15개까지만 노출
        if (principalHandler.isGuestUser()) { // TODO: 메서드화 (게스트 유저와 온보딩 건너뛴 유저)
            if (SpotType.CAFE.equals(SpotType.fromValue(request.condition().spotType()))) {
                throw new BusinessException(ErrorType.NO_PRINCIPAL_ERROR);
            }

            String transportMode; // TODO: 추후 enum 처리
            List<SpotEntity> filteredSpotList = filterSpotList(request, WALKING_RADIUS);

            if (filteredSpotList.isEmpty()) {
                transportMode = "BIKING";
                filteredSpotList = filterSpotList(request, BIKING_RADIUS);
            } else {
                transportMode = "WALKING";
            }

            List<RecommendedSpot> spotList = filteredSpotList.stream()
                    .map(
                            spotEntity -> toRecommendedSpot(
                                    spotEntity,
                                    request.latitude(),
                                    request.longitude(),
                                    transportMode
                            )
                    )
//                    .filter(spot -> !filterOpenAfter10PM || isSpotOpenAfter10PM(spot.spotId()))
                    .limit(15)
                    .toList();

            return new SpotListResponse(transportMode, spotList);
        }

        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        PreferenceEntity preferenceEntity = preferenceRepository.findById(memberEntity.getId()).orElse(null);

        // 1) 필터(거리, 가격, 옵션 등) + 영업중인 가게

        String transportMode; // TODO: 추후 enum 처리
        List<SpotEntity> filteredSpotList = filterSpotList(request, WALKING_RADIUS);

        if (filteredSpotList.isEmpty()) {
            transportMode = "BIKING";
            filteredSpotList = filterSpotList(request, BIKING_RADIUS);
        } else {
            transportMode = "WALKING";
        }

        // TODO: 메서드로 분리
        // ========== [ CASE 1: preferenceEntity가 없는 사용자 ] ==========
        if (preferenceEntity == null) {
            List<RecommendedSpot> spotList = filteredSpotList.stream()
                    .map(
                            spotEntity -> toRecommendedSpot(
                                    spotEntity,
                                    request.latitude(),
                                    request.longitude(),
                                    transportMode
                            )
                    )
//                    .filter(spot -> isSpotOpen(spot.spotId()))
                    .limit(15)
                    .toList();

            return new SpotListResponse(transportMode, spotList);
        }

        // ========== [ CASE 2: preferenceEntity가 있는 사용자 ] ==========
        // 2) 비선호 음식 제외
        filteredSpotList = excludeDislikedSpotList(filteredSpotList, preferenceEntity.getDislikeFoodList());

        List<RecommendedSpot> spotList = filteredSpotList.stream()
                .map(
                        spotEntity -> toRecommendedSpot(
                                spotEntity,
                                request.latitude(),
                                request.longitude(),
                                transportMode
                        )
                )
//                .filter(spot -> isSpotOpen(spot.spotId()))
                .limit(15)
                .toList();

        return new SpotListResponse(transportMode, spotList);
    }

    @Transactional(readOnly = true)
    public boolean checkTestUser() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());

        return memberEntity.getSocialId().equals(testAccount1) || memberEntity.getSocialId().equals(testAccount2)
                || memberEntity.getSocialId().equals(testAccount3) || memberEntity.getSocialId().equals(testAccount4);
    }

    private boolean isOutOfServiceArea(
            final double latitude,
            final double longitude
    ) {
        return latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
                || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE;
    }

    // TODO: 카테고리 필터 enum 처리 급해요
    private List<SpotEntity> filterSpotList(
            final SpotListRequest request,
            final double radius
    ) {
        return spotNativeQueryRepository.findSpotList(
                request.latitude(),
                request.longitude(),
                SpotType.fromValue(request.condition().spotType()),
                request.condition().filterList(),
                radius
        );
    }

    // SpotEntity -> RecommendedSpot 변환 메서드 (게스트 혹은 온보딩 건너뛴 유저)
    private RecommendedSpot toRecommendedSpot(
            final SpotEntity spotEntity,
            final Double latitude,
            final Double longitude,
            final String transportMode
    ) {
        Long spotId = spotEntity.getId();

        return new RecommendedSpot(
                spotId,
                fetchSpotImage(spotId),
                spotEntity.getName(),
                spotEntity.getLocalAcornCount() + spotEntity.getBasicAcornCount(),
                fetchSpotTagList(spotEntity),
                true,
                "23:00",
                "10:00", // TODO: 영업시간 정보 추가
                calculateMovingTime(spotEntity.getId(), latitude, longitude, transportMode),
                spotEntity.getLatitude(),
                spotEntity.getLongitude()
        );
    }

    private String fetchSpotImage(final Long spotId) {
        return spotImageRepository.findTop1BySpotIdOrderById(spotId)
                .map(SpotImageEntity::getImage)
                .orElse(null);
    }

    private List<Tag> fetchSpotTagList(final SpotEntity spotEntity) {
        LocalDateTime now = LocalDateTime.now();
        List<Tag> tagList = new ArrayList<>();

        if (now.isBefore(spotEntity.getCreatedAt().plusMonths(3))) {
            tagList.add(Tag.NEW);
        }

        long localReviewerCount =
                reviewRepository.countDistinctMemberBySpotIdAndAcornCountAndLocalAcorn(spotEntity.getId(), 5, true);

        if (localReviewerCount >= 2) { // TODO: 매직 넘버 yml로 옮기기
            tagList.add(Tag.LOCAL);
        }

        return tagList;
    }

    private int calculateMovingTime(
            final Long spotId,
            final Double latitude,
            final Double longitude,
            final String transportMode
    ) {
        Double distanceMeter = spotRepository.calculateDistanceFromSpot(spotId, longitude, latitude);

        return calculateMovingTimeFromDistance(distanceMeter, transportMode);
    }

    private int calculateMovingTimeFromDistance(
            final Double distanceMeter,
            final String transportMode
    ) {
        // TODO: 타입 확인 및 매직 넘버 yml로 옮기기
        double movingTimeMinutes = 0.0;
        double walkingSpeedMetersPerMinute = (4.5 * 1000.0) / 60.0;
        double bikingSpeedMetersPerMinute = (15.5 * 1000.0) / 60.0;

        if (transportMode.equals("WALKING")) {
            movingTimeMinutes = distanceMeter / walkingSpeedMetersPerMinute;
        } else if (transportMode.equals("BIKING")) {
            movingTimeMinutes = distanceMeter / bikingSpeedMetersPerMinute;
        }

        if (movingTimeMinutes < 1) {
            movingTimeMinutes = 1; // 최소 이동 시간 1분
        }

        return (int) Math.round(movingTimeMinutes);
    }

    private List<SpotEntity> excludeDislikedSpotList(
            final List<SpotEntity> originalList,
            final List<DislikeFood> dislikeFoodList
    ) {
        if (dislikeFoodList == null || dislikeFoodList.isEmpty()) {
            return originalList;
        }

        List<String> dislikedNames = dislikeFoodList.stream()
                .map(Enum::name)
                .toList();

        List<Long> excludedSpotIds = spotOptionRepository.findSpotIdsByOptionNames(dislikedNames);

        return originalList.stream()
                .filter(spot -> !excludedSpotIds.contains(spot.getId()))
                .toList();
    }

    // 메서드 설명: spotId에 해당하는 Spot이 현재 영업 중인지 확인한다. (영업 시간에 속하는지)
    private boolean isSpotOpen(final Long spotId) {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek today = now.getDayOfWeek();
        DayOfWeek yesterday = today.minus(1);
        LocalTime currentTime = now.toLocalTime();

        // 1. 전날 영업 시간에 속하는지 확인 (자정이 지났을 때)
        List<OpeningHourEntity> yesterdayHours = openingHourRepository.findAllBySpotIdAndDayOfWeek(spotId, yesterday);
        boolean isOpenFromYesterday = yesterdayHours.stream()
                .anyMatch(openingHour -> isAfterMidnight(currentTime, openingHour));

        if (isOpenFromYesterday) {
            return true;
        }

        // 2. 오늘 영업 시간에 속하는지 확인 (자정이 지나기 전)
        List<OpeningHourEntity> todayHours = openingHourRepository.findAllBySpotIdAndDayOfWeek(spotId, today);

        return todayHours.stream()
                .anyMatch(openingHour -> isBeforeMidnight(currentTime, openingHour));
    }

    // 메서드 설명: currentTime이 영업 시간에 속하는지 확인한다. (자정이 지난 후)
    private boolean isAfterMidnight(final LocalTime currentTime, final OpeningHourEntity openingHour) {
        LocalTime startTime = openingHour.getStartTime();
        LocalTime endTime = openingHour.getEndTime();

        return endTime.isBefore(startTime) && currentTime.isAfter(LocalTime.MIDNIGHT) && currentTime.isBefore(endTime);
    }

    // 메서드 설명: currentTime이 영업 시간에 속하는지 확인한다. (자정이 지나기 전)
    private boolean isBeforeMidnight(final LocalTime currentTime, final OpeningHourEntity openingHour) {
        LocalTime startTime = openingHour.getStartTime();
        LocalTime endTime = openingHour.getEndTime();

        if (endTime.isBefore(startTime)) {
            return currentTime.isAfter(startTime) && currentTime.isBefore(LocalTime.MIDNIGHT);
        }

        return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
    }

    // TODO: 트랜잭션 범위 고민하기
    // 메서드 설명: spotId에 해당하는 Spot의 상세 정보를 조회한다. (메뉴, 이미지, 영업 여부 등)
    @Transactional
    public SpotDetailResponse fetchSpotDetail(
            final long spotId,
            final boolean isDeepLink
    ) {
        SpotEntity spotEntity = spotRepository.findByIdOrElseThrow(spotId);

        List<SpotImageEntity> spotImageEntityList = spotImageRepository.findAllBySpotIdOrderById(spotId);
        List<String> imageList = spotImageEntityList.stream()
                .map(SpotImageEntity::getImage)
                .toList();

        return new SpotDetailResponse(
                spotId,
                imageList,
                spotEntity.getName(),
                spotEntity.getLocalAcornCount() + spotEntity.getBasicAcornCount(),
                fetchSpotTagList(spotEntity),
                true,
                "23:00",
                "10:00", // TODO: 영업시간 정보 추가
                menuboardImageRepository.existsBySpotId(spotId),
                checkIsSaved(spotId, isDeepLink),
                fetchMenus(spotId),
                spotEntity.getLatitude(),
                spotEntity.getLongitude()
        );
    }

    private boolean checkIsSaved(
            final long spotId,
            final boolean isDeepLink
    ) {
        if (principalHandler.isGuestUser()) {
            if (!isDeepLink) {
                throw new BusinessException(ErrorType.NO_PRINCIPAL_ERROR);
            }

            return false;
        }

        return savedSpotRepository.existsByMemberIdAndSpotId(fetchMemberId(), spotId);
    }

    private long fetchMemberId() {
        long memberId = principalHandler.getMemberIdFromPrincipal();

        if (!memberRepository.existsById(memberId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_MEMBER_ERROR);
        }

        return memberId;
    }

    private List<MenuResponse> fetchMenus(final Long spotId) {
        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        List<MenuEntity> menuEntityList = menuRepository.findAllBySpotId(spotId);

        return menuEntityList.stream()
                .map(menu -> MenuResponse.of(menu.getName(), menu.getPrice()))
                .toList();
    }

    @Transactional(readOnly = true)
    public MenuboardImageListResponse fetchMenuboards(final Long spotId) {
        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        List<String> menuboardImageList = menuboardImageRepository.findAllBySpotIdOrderById(spotId).stream()
                .map(MenuboardImageEntity::getImage)
                .toList();

        return MenuboardImageListResponse.of(menuboardImageList);
    }

    @Transactional(readOnly = true)
    public SearchSuggestionListResponse fetchSearchSuggestions(
            final Double latitude,
            final Double longitude
    ) {
        if (isOutOfServiceArea(latitude, longitude)) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());

        List<SearchSuggestionResponse> recentSpotSuggestion = guidedSpotCustomRepository.findRecentGuidedSpotSuggestions(
                memberEntity.getId(),
                latitude,
                longitude,
                SUGGESTION_RADIUS,
                SUGGESTION_LIMIT
        );

        if (recentSpotSuggestion.size() < SUGGESTION_LIMIT) {
            int needed = SUGGESTION_LIMIT - recentSpotSuggestion.size();

            List<SearchSuggestionResponse> nearestSpotList =
                    findNearestSpotList(longitude, latitude, SUGGESTION_RADIUS, SUGGESTION_LIMIT);

            // Set을 통한 필터링 성능 향상
            Set<Long> existingSpotIds = recentSpotSuggestion.stream()
                    .map(SearchSuggestionResponse::spotId)
                    .collect(Collectors.toSet());

            List<SearchSuggestionResponse> filteredNearestSpotList = nearestSpotList.stream()
                    .filter(nearestSpot -> !existingSpotIds.contains(nearestSpot.spotId()))
                    .limit(needed)
                    .toList();

            recentSpotSuggestion = Stream.concat(
                    recentSpotSuggestion.stream(),
                    filteredNearestSpotList.stream()
            ).toList();
        }

        return new SearchSuggestionListResponse(recentSpotSuggestion);
    }

    // TODO: limit 없는 메서드로부터 분기하도록 리팩토링
    private List<SearchSuggestionResponse> findNearestSpotList(
            final double longitude,
            final double latitude,
            final double radius,
            final int limit
    ) {
        List<Object[]> rawFindResults = spotRepository.findNearestSpotList(longitude, latitude, radius, limit);

        return rawFindResults.stream()
                .map(result -> new SearchSuggestionResponse((Long) result[0], (String) result[1]))
                .toList();
    }

    @Transactional(readOnly = true)
    public SearchSpotListResponse searchSpot(final String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new SearchSpotListResponse(Collections.emptyList());
        }

        List<SpotEntity> spotEntityList = spotRepository.findTop10ByNameStartingWithIgnoreCase(keyword);

        if (spotEntityList.size() < SEARCH_LIMIT) {
            List<SpotEntity> additionalSpots = spotRepository.findByNameContainingWithLimitIgnoreCase(
                    keyword, SEARCH_LIMIT - spotEntityList.size()
            );

            Set<Long> existingSpotIds = spotEntityList.stream()
                    .map(SpotEntity::getId)
                    .collect(Collectors.toSet());

            spotEntityList.addAll(
                    additionalSpots.stream()
                            .filter(additionalSpot -> !existingSpotIds.contains(additionalSpot.getId()))
                            .toList()
            );
        }

        // TODO: mapper로 변경
        List<SearchSpotResponse> spotList = spotEntityList.stream()
                .map(spotEntity -> SearchSpotResponse.builder()
                        .spotId(spotEntity.getId())
                        .name(spotEntity.getName())
                        .address(spotEntity.getAddress())
                        .spotType(spotEntity.getSpotType())
                        .build())
                .toList();

        return new SearchSpotListResponse(spotList);
    }

    @Transactional(readOnly = true)
    public boolean verifySpot(
            final long spotId,
            final double latitude,
            final double longitude
    ) {
        if (isOutOfServiceArea(latitude, longitude)) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        Double distance = spotRepository.calculateDistanceFromSpot(spotId, longitude, latitude);

        if (distance == null) {
            return false;
        }

//        return distance <= VERIFICATION_DISTANCE;
        return true; // TODO: 앱 출시 초기 단계에서 리뷰 작성 시 거리 제한 미적용, 추후 다시 도입 예정
    }

    @Transactional(readOnly = true)
    public Double calculateDistance(
            final Long spotId,
            final Double latitude,
            final Double longitude
    ) {
        return spotRepository.calculateDistanceFromSpot(spotId, longitude, latitude);
    }
}
