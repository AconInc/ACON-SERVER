package com.acon.server.review.application.service;

import com.acon.server.global.auth.PrincipalHandler;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.member.application.mapper.MemberMapper;
import com.acon.server.member.domain.entity.Member;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.member.infra.repository.VerifiedAreaRepository;
import com.acon.server.review.application.mapper.ReviewMapper;
import com.acon.server.review.domain.entity.Review;
import com.acon.server.review.infra.repository.ReviewRepository;
import com.acon.server.spot.application.mapper.SpotMapper;
import com.acon.server.spot.domain.entity.Spot;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final VerifiedAreaRepository verifiedAreaRepository;
    private final ReviewRepository reviewRepository;
    private final SpotRepository spotRepository;

    private final MemberMapper memberMapper;
    private final ReviewMapper reviewMapper;
    private final SpotMapper spotMapper;

    private final PrincipalHandler principalHandler;

    @Transactional
    public void createReview(final long spotId, final int acornCount) {
        MemberEntity memberEntity = memberRepository.findByIdOrElseThrow(principalHandler.getMemberIdFromPrincipal());
        SpotEntity spotEntity = spotRepository.findByIdOrElseThrow(spotId);

//        validateAcornAvailability(memberEntity.getLeftAcornCount(), acornCount); // Acon 2.0 정책: 도토리 무제한

        Member member = memberMapper.toDomain(memberEntity);
        Spot spot = spotMapper.toDomain(spotEntity);

        boolean isLocal = isVerifiedArea(member.getId(), spot.getLegalDong());

//        member.useAcorn(acornCount); // Acon 2.0 정책: 도토리 무제한
        spot.addAcorn(acornCount, isLocal);

        Review review = Review.builder()
                .spotId(spotId)
                .memberId(memberEntity.getId())
                .acornCount(acornCount)
                .localAcorn(isLocal)
                .build();

        memberRepository.save(memberMapper.toEntity(member));
        spotRepository.save(spotMapper.toEntity(spot));
        reviewRepository.save(reviewMapper.toEntity(review));
    }

    private void validateAcornAvailability(int leftAcornCount, int acornCount) {
        if (leftAcornCount < acornCount) {
            throw new BusinessException(ErrorType.INSUFFICIENT_ACORN_COUNT_ERROR);
        }
    }

    private boolean isVerifiedArea(long memberId, String spotName) {
        return verifiedAreaRepository.existsByMemberIdAndName(memberId, spotName);
    }
}
