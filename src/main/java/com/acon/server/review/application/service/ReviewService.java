package com.acon.server.review.application.service;

import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.member.application.mapper.MemberMapper;
import com.acon.server.member.domain.entity.Member;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.member.infra.repository.VerifiedAreaRepository;
import com.acon.server.review.api.response.ReviewAvailabilityResponse;
import com.acon.server.review.application.mapper.ReviewMapper;
import com.acon.server.review.application.model.ReviewContext;
import com.acon.server.review.domain.entity.Review;
import com.acon.server.review.infra.repository.ReviewRepository;
import com.acon.server.spot.application.mapper.SpotMapper;
import com.acon.server.spot.domain.entity.Spot;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final double MIN_LATITUDE = 33.1;
    private static final double MAX_LATITUDE = 38.6;
    private static final double MIN_LONGITUDE = 124.6;
    private static final double MAX_LONGITUDE = 131.9;
    private static final int VERIFICATION_DISTANCE = 250;

    private final MemberRepository memberRepository;
    private final VerifiedAreaRepository verifiedAreaRepository;
    private final ReviewRepository reviewRepository;
    private final SpotRepository spotRepository;

    private final MemberMapper memberMapper;
    private final ReviewMapper reviewMapper;
    private final SpotMapper spotMapper;

    private final PrincipalHandler principalHandler;

    @Value("${google.test-account-1}")
    private String testAccount1;

    @Value("${google.test-account-2}")
    private String testAccount2;

    @Value("${google.test-account-3}")
    private String testAccount3;

    @Value("${google.test-account-4}")
    private String testAccount4;

    // TODO: 순환 참조 방지를 위해 같은 메서드를 재선언했으므로, 추후 Facade 패턴을 통한 리팩토링 필요
    @Transactional(readOnly = true)
    public boolean checkTestUser() {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());

        return memberEntity.getSocialId().equals(testAccount1) || memberEntity.getSocialId().equals(testAccount2)
                || memberEntity.getSocialId().equals(testAccount3) || memberEntity.getSocialId().equals(testAccount4);
    }

    @Transactional(readOnly = true)
    public ReviewAvailabilityResponse verifyReviewAvailability(
            final long spotId,
            final double latitude,
            final double longitude
    ) {
        boolean available;

        if (isOutOfServiceArea(latitude, longitude)) {
            throw new BusinessException(ErrorType.UNAVAILABLE_SERVICE_AREA_ERROR);
        }

        if (!spotRepository.existsById(spotId)) {
            throw new BusinessException(ErrorType.NOT_FOUND_SPOT_ERROR);
        }

        Double distance = spotRepository.calculateDistanceToSpot(spotId, longitude, latitude);

        if (distance == null) {
            available = false;
        } else {
//            available = distance <= VERIFICATION_DISTANCE;
            available = true; // TODO: 앱 출시 초기 단계에서 리뷰 작성 시 거리 제한 미적용, 추후 다시 도입 예정
        }

        return ReviewAvailabilityResponse.of(available);
    }

    // TODO: 공통 메서드로 빼기
    private boolean isOutOfServiceArea(
            final double latitude,
            final double longitude
    ) {
        return latitude < MIN_LATITUDE || latitude > MAX_LATITUDE
                || longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE;
    }

    @Transactional
    public void createReview(final long spotId, final int acornCount) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        SpotEntity spotEntity = spotRepository.findByIdOrElseThrow(spotId);

        Member member = memberMapper.toDomain(memberEntity);
        Spot spot = spotMapper.toDomain(spotEntity);

        ReviewContext context = buildReviewContext(member, spot, acornCount, null);

        memberRepository.save(memberMapper.toEntity(context.member()));
        spotRepository.save(spotMapper.toEntity(context.spot()));
        reviewRepository.save(reviewMapper.toEntity(context.review()));
    }

    @Transactional
    public void createReview(
            final long spotId,
            final String recommendedMenu,
            final int acornCount
    ) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        SpotEntity spotEntity = spotRepository.findByIdOrElseThrow(spotId);

        Member member = memberMapper.toDomain(memberEntity);
        Spot spot = spotMapper.toDomain(spotEntity);

        ReviewContext context = buildReviewContext(member, spot, acornCount, recommendedMenu);

        memberRepository.save(memberMapper.toEntity(context.member()));
        spotRepository.save(spotMapper.toEntity(context.spot()));
        reviewRepository.save(reviewMapper.toEntity(context.review()));
    }

    private ReviewContext buildReviewContext(
            Member member,
            Spot spot,
            int acornCount,
            String recommendedMenu
    ) {
//        validateDropAcornAvailability(memberEntity.getLeftAcornCount(), acornCount); // Acon 2.0 정책: 도토리 무제한

        boolean isLocal = isVerifiedArea(member.getId(), spot.getLegalDong());

//        member.useAcorn(acornCount); // Acon 2.0 정책: 도토리 무제한
        spot.addAcorn(acornCount, isLocal);

        Review review = Review.builder()
                .spotId(spot.getId())
                .memberId(member.getId())
                .acornCount(acornCount)
                .recommendedMenu(recommendedMenu)
                .localAcorn(isLocal)
                .build();

        return ReviewContext.of(member, spot, review);
    }

    private void validateDropAcornAvailability(int leftAcornCount, int acornCount) {
        if (leftAcornCount < acornCount) {
            throw new BusinessException(ErrorType.INSUFFICIENT_ACORN_COUNT_ERROR);
        }
    }

    private boolean isVerifiedArea(long memberId, String spotName) {
        return verifiedAreaRepository.existsByMemberIdAndName(memberId, spotName);
    }
}
