package com.acon.server.spot.application.service;

import static com.acon.server.member.domain.enums.Cuisine.matchCuisine;
import static com.acon.server.member.domain.enums.FavoriteSpot.matchFavoriteSpot;
import static com.acon.server.member.domain.enums.SpotStyle.matchSpotStyle;

import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.global.external.maps.GeoCodingResponse;
import com.acon.server.global.external.maps.NaverMapsAdapter;
import com.acon.server.member.domain.enums.Cuisine;
import com.acon.server.member.domain.enums.DislikeFood;
import com.acon.server.member.domain.enums.FavoriteSpot;
import com.acon.server.member.domain.enums.SpotStyle;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.entity.PreferenceEntity;
import com.acon.server.member.infra.repository.GuidedSpotCustomRepository;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.member.infra.repository.PreferenceRepository;
import com.acon.server.spot.api.request.SpotListRequest;
import com.acon.server.spot.api.response.MenuListResponse;
import com.acon.server.spot.api.response.MenuResponse;
import com.acon.server.spot.api.response.SearchSpotListResponse;
import com.acon.server.spot.api.response.SearchSpotResponse;
import com.acon.server.spot.api.response.SearchSuggestionListResponse;
import com.acon.server.spot.api.response.SearchSuggestionResponse;
import com.acon.server.spot.api.response.SpotDetailResponse;
import com.acon.server.spot.api.response.SpotListResponse;
import com.acon.server.spot.api.response.SpotListResponse.RecommendedSpot;
import com.acon.server.spot.application.mapper.SpotDtoMapper;
import com.acon.server.spot.application.mapper.SpotMapper;
import com.acon.server.spot.domain.entity.Spot;
import com.acon.server.spot.domain.enums.SpotType;
import com.acon.server.spot.domain.vo.SpotWithScore;
import com.acon.server.spot.infra.entity.MenuEntity;
import com.acon.server.spot.infra.entity.OpeningHourEntity;
import com.acon.server.spot.infra.entity.OptionEntity;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.entity.SpotImageEntity;
import com.acon.server.spot.infra.entity.SpotOptionEntity;
import com.acon.server.spot.infra.repository.MenuRepository;
import com.acon.server.spot.infra.repository.OpeningHourRepository;
import com.acon.server.spot.infra.repository.OptionRepository;
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
    private final MenuRepository menuRepository;
    private final OpeningHourRepository openingHourRepository;
    private final OptionRepository optionRepository;
    private final SpotImageRepository spotImageRepository;
    private final SpotNativeQueryRepository spotNativeQueryRepository;
    private final SpotOptionRepository spotOptionRepository;
    private final SpotRepository spotRepository;

    private final SpotDtoMapper spotDtoMapper;
    private final SpotMapper spotMapper;

    private final PrincipalHandler principalHandler;

    private final NaverMapsAdapter naverMapsAdapter;

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
                .map(spotEntity -> {
                    Spot spot = spotMapper.toDomain(spotEntity);
                    updateSpotCoordinate(spot);
                    return spotMapper.toEntity(spot);
                })
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
        spot.updateLegalDong(
                naverMapsAdapter.getReverseGeoCodingResult(spot.getLatitude(), spot.getLongitude())
        );
        spot.updateCreatedAt();
    }

    @Transactional(readOnly = true)
    public SpotListResponse fetchRecommendedSpotList(final SpotListRequest request) {
        if (isOutOfServiceArea(request.latitude(), request.longitude())) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        // TODO: 토글, 상세필터, 상세페이지, 길찾기 다 게스트 유저 접근 불가
        // TODO: 장소는 최대 5개까지만 노출
        if (principalHandler.isGuestUser()) { // TODO: 메서드화 (게스트 유저와 온보딩 건너뛴 유저)
            List<SpotEntity> filteredSpotList = filterSpotList(request);
            List<SpotEntity> mutableList = new ArrayList<>(filteredSpotList);

            // 무작위로 6개 추출
            Collections.shuffle(mutableList);

            List<RecommendedSpot> spotList = mutableList.stream()
                    .map(spotEntity -> toRecommendedSpot(spotEntity, request.latitude(), request.longitude()))
                    .limit(6)
                    .toList();

            return new SpotListResponse(spotList);
        }

        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());
        PreferenceEntity preferenceEntity = preferenceRepository.findByMemberId(memberEntity.getId()).orElse(null);

        // 1) 필터(거리, 가격, 옵션 등) + 영업중인 가게
        List<SpotEntity> filteredSpotList = filterSpotList(request);
//        filteredSpotList = filteredSpotList.stream()
//                .filter(spot -> isSpotOpen(spot.getId()))
//                .toList();

        // TODO: 메서드로 분리
        // ========== [ CASE 1: preferenceEntity가 없는 사용자 ] ==========
        if (preferenceEntity == null) {
            // 가변 리스트 생성
            List<SpotEntity> mutableList = new ArrayList<>(filteredSpotList);

            // 무작위로 6개 추출
            Collections.shuffle(mutableList);

            List<RecommendedSpot> spotList = mutableList.stream()
                    .map(spotEntity -> toRecommendedSpot(spotEntity, request.latitude(), request.longitude()))
                    .limit(6)
                    .toList();

            return new SpotListResponse(spotList);
        }

        // ========== [ CASE 2: preferenceEntity가 있는 사용자 ] ==========
        // 2) 비선호 음식 제외
        filteredSpotList = excludeDislikedSpotList(filteredSpotList, preferenceEntity.getDislikeFoodList());

        // 3) Spot별 점수/매칭률 계산
        List<SpotWithScore> scoredList = filteredSpotList.stream()
                .map(spotEntity -> {
                    double score = calculateSpotScore(
                            spotEntity,
                            preferenceEntity,
                            request.latitude(),
                            request.longitude()
                    );
                    int matchingRate = calculateMatchingRate(score, spotEntity.getSpotType());

                    return new SpotWithScore(spotEntity, score, matchingRate);
                })
                .sorted((a, b) -> {
                    // 1) matchingRate 내림차순
                    int rateCompare = Integer.compare(b.matchingRate(), a.matchingRate());
                    if (rateCompare != 0) {
                        return rateCompare;
                    }

                    // 2) matchingRate가 같은 경우 createdAt 내림차순
                    LocalDateTime aCreated = a.spotEntity().getCreatedAt();
                    LocalDateTime bCreated = b.spotEntity().getCreatedAt();

                    // TODO: 추후 Comparator로 분리
                    // createdAt이 null일 수도 있으니 안전 처리
                    if (bCreated == null && aCreated == null) {
                        return 0;
                    } else if (bCreated == null) {
                        // b가 null이면 a가 더 최근이므로 a가 앞으로
                        return -1;
                    } else if (aCreated == null) {
                        // a가 null이면 b가 더 최근이므로 b가 앞으로
                        return 1;
                    }
                    // 둘 다 null이 아니면, b가 더 최근이면 양수 → b가 앞으로
                    return bCreated.compareTo(aCreated);
                })
                .limit(6)
                .toList();

        // 4) DTO 변환
        List<RecommendedSpot> spotList = scoredList.stream()
                .map(spotWithScore ->
                        toRecommendedSpot(spotWithScore.spotEntity(), spotWithScore.matchingRate(), request.latitude(),
                                request.longitude())
                )
                .collect(Collectors.toList());

        return new SpotListResponse(spotList);
    }

    // TODO: enum 처리 급해요
    private List<SpotEntity> filterSpotList(final SpotListRequest request) {
        return spotNativeQueryRepository.findSpotsWithinDistance(
                request.latitude(),
                request.longitude(),
                calculateDistanceFromWalkingTime(request.condition().walkingTime()),
                request.condition().spotType(),
                request.condition().priceRange(),
                request.condition().filterList()
        );
    }

    private double calculateDistanceFromWalkingTime(final Integer walkingTime) {
        if (walkingTime == null) {
            return 2000.0;
        }
        // TODO: 매직 넘버 yml로 옮기기
        double distanceKm = (walkingTime / 60.0) * 4.0;
        return distanceKm * 1000.0;
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

    private double calculateSpotScore(
            final SpotEntity spotEntity,
            final PreferenceEntity preferenceEntity,
            final double userLat,
            final double userLon
    ) {
        // TODO: 매직 넘버 yml로 옮기기
        double score = 0.0;

        if (preferenceEntity.getFavoriteSpotType() == spotEntity.getSpotType()) {
            score += 6.0;
        } else {
            score += 3.0;
        }

        List<OptionEntity> optionList = findOptionsBySpot(spotEntity.getId());

        score += calcCuisineScore(optionList, preferenceEntity.getFavoriteCuisineRank());

        score += calcSpotStyleScore(optionList, preferenceEntity.getFavoriteSpotStyle());

        score += calcFavoriteSpotScore(optionList, preferenceEntity.getFavoriteSpotRank());

        score += calcAcornScore(spotEntity);

        // TODO: 메서드로 분리
        // TODO: 매직 넘버 yml로 옮기기
        if (spotEntity.getCreatedAt() != null) {
            LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
            if (spotEntity.getCreatedAt().isAfter(threeMonthsAgo)) {
                score += 3.0;
            }
        }

        Double distanceMeter = spotRepository.calculateDistanceFromSpot(spotEntity.getId(), userLon, userLat);
        // TODO: 매직 넘버 yml로 옮기기
        if (distanceMeter != null && distanceMeter <= 3000) {
            double distanceBonus = (distanceMeter / 100.0) * 0.1;
            distanceBonus = Math.min(distanceBonus, 3.0);
            score += (3.0 - distanceBonus);
        }

        return score;
    }

    private List<OptionEntity> findOptionsBySpot(final Long spotId) {
        List<SpotOptionEntity> spotOptionList = spotOptionRepository.findAllBySpotId(spotId);

        List<Long> optionIds = spotOptionList.stream()
                .map(SpotOptionEntity::getOptionId)
                .toList();

        return optionRepository.findAllById(optionIds);
    }

    private double calcCuisineScore(final List<OptionEntity> optionList, final List<Cuisine> favoriteCuisines) {
        double resultScore = 0.0; // TODO: 매직 넘버 yml로 옮기기

        if (favoriteCuisines == null || favoriteCuisines.isEmpty()) {
            return resultScore;
        }

        for (OptionEntity option : optionList) {
            String optionName = option.getName();

            if (optionName == null) {
                continue;
            }

            Cuisine matchedCuisine = matchCuisine(option.getName());
            if (matchedCuisine == null) {
                continue;
            }

            int idx = favoriteCuisines.indexOf(matchedCuisine);

            // TODO: 매직 넘버 yml로 옮기기
            if (idx >= 0) {
                double candidateScore = switch (idx) {
                    case 0 -> 4.0;
                    case 1 -> 2.0;
                    case 2 -> 1.0;
                    default -> 0.0;
                };

                resultScore = Math.max(resultScore, candidateScore);
            }
        }

        return resultScore;
    }

    private double calcSpotStyleScore(final List<OptionEntity> optionList, final SpotStyle favoriteSpotStyle) {
        if (favoriteSpotStyle == null) {
            return 0.0;
        }

        for (OptionEntity option : optionList) {
            String optionName = option.getName();

            if (optionName == null) {
                continue;
            }

            SpotStyle matchedSpotStyle = matchSpotStyle(option.getName());

            if (matchedSpotStyle != null && matchedSpotStyle == favoriteSpotStyle) {
                return 2.0; // TODO: 매직 넘버 yml로 옮기기
            }
        }

        return 0.0; // TODO: 매직 넘버 yml로 옮기기
    }

    private double calcFavoriteSpotScore(final List<OptionEntity> optionList, final List<FavoriteSpot> favoriteSpots) {
        double resultScore = 0.0; // TODO: 매직 넘버 yml로 옮기기

        if (favoriteSpots == null || favoriteSpots.isEmpty()) {
            return resultScore;
        }

        for (OptionEntity option : optionList) {
            String optionName = option.getName();

            if (optionName == null) {
                continue;
            }

            FavoriteSpot matchedFavoriteSpot = matchFavoriteSpot(option.getName());

            if (matchedFavoriteSpot != null) {
                int idx = favoriteSpots.indexOf(matchedFavoriteSpot);

                // TODO: 매직 넘버 yml로 옮기기
                if (idx >= 0) {
                    double candidateScore = switch (idx) {
                        case 0 -> 4.0;
                        case 1 -> 2.0;
                        case 2 -> 1.0;
                        default -> 0.0;
                    };

                    resultScore = Math.max(resultScore, candidateScore);
                }
            }
        }

        return resultScore;
    }

    private double calcAcornScore(final SpotEntity spotEntity) {
        double acornScore = 0.0; // TODO: 매직 넘버 yml로 옮기기

        // TODO: 매직 넘버 yml로 옮기기
        boolean isRecentLocal = spotEntity.getLocalAcornUpdatedAt() != null
                && spotEntity.getLocalAcornUpdatedAt().isAfter(LocalDateTime.now().minusDays(1));
        boolean isRecentBasic = spotEntity.getBasicAcornUpdatedAt() != null
                && spotEntity.getBasicAcornUpdatedAt().isAfter(LocalDateTime.now().minusDays(1));

        if (isRecentLocal && spotEntity.getLocalAcornCount() >= 4) {
            acornScore += 5.0; // TODO: 매직 넘버 yml로 옮기기
        }

        if (isRecentBasic && spotEntity.getBasicAcornCount() >= 4) {
            acornScore += 2.0; // TODO: 매직 넘버 yml로 옮기기
        }

        return acornScore;
    }

    // SpotEntity -> RecommendedSpot 변환 메서드 (게스트 혹은 온보딩 건너뛴 유저)
    private RecommendedSpot toRecommendedSpot(
            final SpotEntity spotEntity,
            final double latitude,
            final double longitude
    ) {
        return new RecommendedSpot(
                spotEntity.getId(),
                fetchSpotImage(spotEntity.getId()),
                null,
                spotEntity.getSpotType().name(),
                spotEntity.getName(),
                calculateWalkingTime(spotEntity, latitude, longitude)
        );
    }

    // TODO: mapper로 분리
    // SpotEntity -> RecommendedSpot 변환 메서드 (온보딩 마친 유저)
    private RecommendedSpot toRecommendedSpot(
            final SpotEntity spotEntity,
            final int matchingRate,
            final Double latitude,
            final Double longitude
    ) {
        return new RecommendedSpot(
                spotEntity.getId(),
                fetchSpotImage(spotEntity.getId()),
                matchingRate,
                spotEntity.getSpotType().name(),
                spotEntity.getName(),
                calculateWalkingTime(spotEntity, latitude, longitude)
        );
    }

    private String fetchSpotImage(final Long spotId) {
        return spotImageRepository.findTopBySpotId(spotId)
                .map(SpotImageEntity::getImage)
                // TODO: 추후 기본 이미지로 변경
                .orElse("https://github.com/user-attachments/assets/52e88bff-2577-4f0e-ae98-9636f88afd2a");
    }

    private int calculateMatchingRate(final double score, final SpotType spotType) {
        // TODO: 매직 넘버 yml로 옮기기
        double maxScore = 0.0;
        switch (spotType) {
            case RESTAURANT -> maxScore = 27.0;
            case CAFE -> maxScore = 23.0;
        }

        // score가 maxScore보다 높아도 결과가 100을 넘지 않도록 ratio를 1.0 이하로 고정
        double ratio = Math.min(1.0, score / maxScore);
        double rawRate = 80.0 + (ratio * 20.0);

        return (int) Math.round(rawRate);
    }

    private int calculateWalkingTime(final SpotEntity spotEntity, final Double latitude, final Double longitude) {
        Double distanceMeter = spotRepository.calculateDistanceFromSpot(spotEntity.getId(), longitude, latitude);

        return calculateWalkingTimeFromDistance(distanceMeter);
    }

    private int calculateWalkingTimeFromDistance(final double distanceMeter) {
        // TODO: 매직 넘버 yml로 옮기기
        // 시속 4km -> 분당 이동 거리: (4km / 60분) = 0.0667km/min = 66.7m/min
        double walkingSpeedMetersPerMinute = (4.0 * 1000.0) / 60.0;

        // 걷는 시간(분) = 거리(m) / 분당 이동 거리(m/min)
        double walkingTimeMinutes = distanceMeter / walkingSpeedMetersPerMinute;

        // 소수점 이하를 반올림하여 정수로 반환
        return (int) Math.round(walkingTimeMinutes);
    }

    // TODO: 장소 추천 시 메뉴 가격 변동이면 메인 메뉴 X 처리

    // 매칭률 같을 때 띄우는 순서

    // TODO: 트랜잭션 범위 고민하기
    // 메서드 설명: spotId에 해당하는 Spot의 상세 정보를 조회한다. (메뉴, 이미지, 영업 여부 등)
    @Transactional
    public SpotDetailResponse fetchSpotDetail(final Long spotId) {
        SpotEntity spotEntity = spotRepository.findByIdOrElseThrow(spotId);
        Spot spot = spotMapper.toDomain(spotEntity);

        List<SpotImageEntity> spotImageEntityList = spotImageRepository.findAllBySpotId(spotId);
        List<String> imageList = spotImageEntityList.stream()
                .map(SpotImageEntity::getImage)
                .toList();

        if (spot.getLatitude() == null || spot.getLongitude() == null) {
            updateSpotCoordinate(spot);
            spotEntity = spotRepository.save(spotMapper.toEntity(spot));
        }

        return spotDtoMapper.toSpotDetailResponse(spotEntity, imageList, isSpotOpen(spotId));
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

    @Transactional(readOnly = true)
    public MenuListResponse fetchMenus(final Long spotId) {
        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        List<MenuEntity> menuEntityList = menuRepository.findAllBySpotId(spotId);

        // TODO: mapper로 변경
        List<MenuResponse> menuResponseList = menuEntityList.stream()
                .map(menu -> MenuResponse.builder()
                        .id(menu.getId())
                        .name(menu.getName())
                        .price(menu.getPrice())
                        .image(menu.getImage())
                        .build())
                .toList();

        return new MenuListResponse(menuResponseList);
    }

    @Transactional(readOnly = true)
    public SearchSuggestionListResponse fetchSearchSuggestions(
            final Double latitude,
            final Double longitude
    ) {
        if (isOutOfServiceArea(latitude, longitude)) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

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
    public boolean checkTestUser() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getUserIdFromPrincipal());

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
}
