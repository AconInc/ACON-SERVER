package com.acon.server.admin.application.service;

import com.acon.server.admin.api.request.CreateSpotRequest;
import com.acon.server.admin.api.response.AdminSpotDetailResponse;
import com.acon.server.admin.api.response.DashboardResponse;
import com.acon.server.admin.api.response.SpotListResponse;
import com.acon.server.admin.api.response.SpotListResponse.SpotItem;
import com.acon.server.admin.domain.enums.MissingField;
import com.acon.server.admin.domain.enums.QueryTarget;
import com.acon.server.admin.infra.entity.AdminEntity;
import com.acon.server.admin.infra.repository.AdminRepository;
import com.acon.server.admin.infra.repository.AdminSpotRepository;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.global.external.s3.S3Adapter;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.review.infra.repository.ReviewRepository;
import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.domain.enums.SpotType;
import com.acon.server.spot.infra.entity.MenuEntity;
import com.acon.server.spot.infra.entity.MenuboardImageEntity;
import com.acon.server.spot.infra.entity.OpeningHourEntity;
import com.acon.server.spot.infra.entity.OptionEntity;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.entity.SpotImageEntity;
import com.acon.server.spot.infra.entity.SpotOptionEntity;
import com.acon.server.spot.infra.repository.CategoryRepository;
import com.acon.server.spot.infra.repository.MenuRepository;
import com.acon.server.spot.infra.repository.MenuboardImageRepository;
import com.acon.server.spot.infra.repository.OpeningHourRepository;
import com.acon.server.spot.infra.repository.OptionRepository;
import com.acon.server.spot.infra.repository.SpotImageRepository;
import com.acon.server.spot.infra.repository.SpotOptionRepository;
import com.acon.server.spot.infra.repository.SpotRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SpotRepository spotRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final AdminSpotRepository adminSpotRepository;
    private final CategoryRepository categoryRepository;
    private final OptionRepository optionRepository;
    private final SpotOptionRepository spotOptionRepository;
    private final OpeningHourRepository openingHourRepository;
    private final MenuRepository menuRepository;
    private final MenuboardImageRepository menuboardImageRepository;
    private final SpotImageRepository spotImageRepository;
    private final ReviewRepository reviewRepository;
    private final S3Adapter s3Adapter;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        int pendingSpotCount = (int) spotRepository.countBySpotStatus(SpotStatus.PENDING);

        return DashboardResponse.of(pendingSpotCount);
    }

    @Transactional(readOnly = true)
    public SpotListResponse getSpots(
            final String query,
            final QueryTarget queryTarget,
            final List<SpotStatus> spotStatusList,
            final MissingField missingField
    ) {
        List<SpotEntity> spotEntityList = adminSpotRepository.findSpotsByFilters(
                query,
                queryTarget,
                spotStatusList,
                missingField
        );

        List<SpotItem> spotItemList = spotEntityList.stream()
                .map(spotEntity -> {
                    String userNickname;

                    if (spotEntity.getAppliedUserId() == null) {
                        userNickname = "ADMIN";
                    } else if (Boolean.TRUE.equals(spotEntity.getAppliedByMember())) {
                        MemberEntity memberEntity = memberRepository.findById(spotEntity.getAppliedUserId())
                                .orElse(null);
                        userNickname = memberEntity != null ? memberEntity.getNickname() : "탈퇴한 유저";
                    } else {
                        AdminEntity adminEntity = adminRepository.findById(spotEntity.getAppliedUserId())
                                .orElse(null);
                        userNickname = adminEntity != null ? adminEntity.getUsername() + "(ADMIN)" : "탈퇴한 어드민";
                    }

                    return SpotItem.of(
                            spotEntity.getId(),
                            userNickname,
                            spotEntity.getName(),
                            spotEntity.getSpotStatus().name(),
                            spotEntity.getSpotType().name(),
                            spotEntity.getUpdatedAt()
                    );
                })
                .toList();

        return SpotListResponse.of(spotItemList);
    }

    @Transactional
    public void createSpot(final CreateSpotRequest request) {
        // 1. Admin ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AdminEntity admin = adminRepository.findByUsernameOrElseThrow(username);
        Long adminId = admin.getId();

        // 2. 동일한 장소명과 주소를 가진 장소가 활성화되어 있는지 확인
        boolean exists = spotRepository.existsByNameAndAddressAndSpotStatus(
                request.spotName(),
                request.address(),
                SpotStatus.ACTIVE
        );

        if (exists) {
            throw new BusinessException(ErrorType.DUPLICATE_ACTIVE_SPOT_ERROR);
        }

        // 3. Spot 엔티티 생성 및 저장
        SpotEntity spot = spotRepository.save(
                SpotEntity.builder()
                        .name(request.spotName())
                        .spotType(request.spotType())
                        .localAcornCount(request.localAcornCount())
                        .basicAcornCount(request.basicAcornCount())
                        .address(request.address())
                        .appliedUserId(adminId)
                        .appliedByMember(false) // Admin이 등록
                        .spotStatus(SpotStatus.ACTIVE)
                        .build()
        );

        Long spotId = spot.getId();

        // 4. SpotFeature 처리
        if (request.spotFeatureList() != null && !request.spotFeatureList().isEmpty()) {
            String categoryName = request.spotType() == SpotType.RESTAURANT
                    ? "RESTAURANT_FEATURE"
                    : "CAFE_FEATURE";

            Long categoryId = categoryRepository.findByNameOrElseThrow(categoryName).getId();

            List<SpotOptionEntity> spotOptionEntityList = new ArrayList<>();

            for (String spotFeature : request.spotFeatureList()) {
                OptionEntity optionEntity;

                try {
                    optionEntity = optionRepository.findByCategoryIdAndNameOrElseThrow(categoryId, spotFeature);
                } catch (BusinessException e) {
                    throw new BusinessException(ErrorType.INVALID_SPOT_FEATURE_ERROR);
                }

                spotOptionEntityList.add(
                        SpotOptionEntity.builder()
                                .spotId(spotId)
                                .optionId(optionEntity.getId())
                                .build()
                );
            }

            spotOptionRepository.saveAll(spotOptionEntityList);
        }

        // 5. PriceFeature 처리
        if (request.priceFeature() != null) {
            Long categoryId = categoryRepository.findByNameOrElseThrow("PRICE").getId();

            OptionEntity optionEntity;

            try {
                optionEntity = optionRepository.findByCategoryIdAndNameOrElseThrow(categoryId, request.priceFeature());
            } catch (BusinessException e) {
                throw new BusinessException(ErrorType.INVALID_PRICE_FEATURE_ERROR);
            }

            spotOptionRepository.save(
                    SpotOptionEntity.builder()
                            .spotId(spotId)
                            .optionId(optionEntity.getId())
                            .build()
            );
        }

        // 6. 영업 시간 저장
        List<OpeningHourEntity> openingHours = request.openingHourList().stream()
                .map(openingHour -> OpeningHourEntity.builder()
                        .spotId(spotId)
                        .dayOfWeek(openingHour.dayOfWeek())
                        .closed(openingHour.closed())
                        .startTime(openingHour.startTime())
                        .endTime(openingHour.endTime())
                        .breakStartTime(openingHour.breakStartTime())
                        .breakEndTime(openingHour.breakEndTime())
                        .build())
                .toList();

        openingHourRepository.saveAll(openingHours);

        // 7. 대표 메뉴 저장
        List<MenuEntity> menus = request.signatureMenuList().stream()
                .map(menu -> MenuEntity.builder()
                        .spotId(spotId)
                        .name(menu.name())
                        .price(menu.price())
                        .build())
                .toList();

        menuRepository.saveAll(menus);

        // 8. 메뉴판 이미지 처리 및 저장
        if (request.menuboardImageList() != null && !request.menuboardImageList().isEmpty()) {
            for (String imageUrl : request.menuboardImageList()) {
                s3Adapter.validateImageExists(imageUrl);
            }

            List<String> movedMenuboardImageList = new ArrayList<>();

            for (String imageUrl : request.menuboardImageList()) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                String destinationKey = String.format("spots/%d/menuboard/%s", spotId, fileName);

                String newImageUrl = s3Adapter.moveFile(imageUrl, destinationKey);
                movedMenuboardImageList.add(newImageUrl);
            }

            List<MenuboardImageEntity> menuboardImageList = movedMenuboardImageList.stream()
                    .map(image -> MenuboardImageEntity.builder()
                            .spotId(spotId)
                            .image(image)
                            .build())
                    .toList();

            menuboardImageRepository.saveAll(menuboardImageList);
        }

        // 9. 장소 이미지 처리 및 저장
        if (request.spotImageList() != null && !request.spotImageList().isEmpty()) {
            for (String imageUrl : request.spotImageList()) {
                s3Adapter.validateImageExists(imageUrl);
            }

            List<String> movedSpotImageList = new ArrayList<>();

            for (String imageUrl : request.spotImageList()) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                String destinationKey = String.format("spots/%d/spot/%s", spotId, fileName);

                String newImageUrl = s3Adapter.moveFile(imageUrl, destinationKey);
                movedSpotImageList.add(newImageUrl);
            }

            List<SpotImageEntity> spotImageList = movedSpotImageList.stream()
                    .map(image -> SpotImageEntity.builder()
                            .spotId(spotId)
                            .image(image)
                            .build())
                    .toList();

            spotImageRepository.saveAll(spotImageList);
        }
    }

    @Transactional(readOnly = true)
    public AdminSpotDetailResponse getSpotDetail(final Long spotId) {
        // 1. Spot 조회
        SpotEntity spot = spotRepository.findByIdOrElseThrow(spotId);

        // 2. 유저 닉네임 조회
        String userNickname;

        if (spot.getAppliedUserId() == null) {
            userNickname = "ADMIN";
        } else if (Boolean.TRUE.equals(spot.getAppliedByMember())) {
            MemberEntity member = memberRepository.findById(spot.getAppliedUserId())
                    .orElse(null);
            userNickname = member != null ? member.getNickname() : "탈퇴한 유저";
        } else {
            AdminEntity admin = adminRepository.findById(spot.getAppliedUserId())
                    .orElse(null);
            userNickname = admin != null ? admin.getUsername() + "(ADMIN)" : "탈퇴한 어드민";
        }

        // 3. SpotFeature 조회
        List<String> spotFeatureList = spotOptionRepository.findSpotFeaturesBySpotId(spotId);

        // 4. PriceFeature 조회
        String priceFeature = spotOptionRepository.findPriceFeatureBySpotId(spotId);

        // 5. 영업시간 조회
        List<AdminSpotDetailResponse.OpeningHourItem> openingHourList = openingHourRepository.findAllBySpotId(spotId)
                .stream()
                .map(openingHour -> AdminSpotDetailResponse.OpeningHourItem.of(
                        openingHour.getDayOfWeek(),
                        openingHour.getClosed(),
                        openingHour.getStartTime(),
                        openingHour.getEndTime(),
                        openingHour.getBreakStartTime(),
                        openingHour.getBreakEndTime()
                ))
                .toList();

        // 6. 대표 메뉴 조회
        List<AdminSpotDetailResponse.SignatureMenu> signatureMenuList = menuRepository.findAllBySpotId(spotId).stream()
                .map(menu -> AdminSpotDetailResponse.SignatureMenu.of(
                        menu.getName(),
                        menu.getPrice()
                ))
                .toList();

        // 7. 추천 메뉴 조회 (리뷰에서 집계)
        List<AdminSpotDetailResponse.RecommendedMenu> recommendedMenuList = reviewRepository
                .findTop3RecommendedMenusBySpotId(spotId).stream()
                .map(projection -> AdminSpotDetailResponse.RecommendedMenu.of(
                        projection.getMenu(),
                        projection.getCount().intValue()
                ))
                .toList();

        // 8. 메뉴판 이미지 조회
        List<String> menuboardImageList = menuboardImageRepository.findAllBySpotIdOrderById(spotId).stream()
                .map(MenuboardImageEntity::getImage)
                .toList();

        // 9. 장소 이미지 조회
        List<String> spotImageList = spotImageRepository.findAllBySpotIdOrderById(spotId).stream()
                .map(SpotImageEntity::getImage)
                .toList();

        return AdminSpotDetailResponse.of(
                spot.getSpotStatus().name(),
                spot.getId(),
                userNickname,
                spot.getUpdatedAt(),
                spot.getName(),
                spot.getAddress(),
                spot.getLocalAcornCount(),
                spot.getBasicAcornCount(),
                spot.getSpotType().name(),
                spotFeatureList,
                openingHourList,
                signatureMenuList,
                recommendedMenuList,
                priceFeature,
                menuboardImageList,
                spotImageList
        );
    }
}
