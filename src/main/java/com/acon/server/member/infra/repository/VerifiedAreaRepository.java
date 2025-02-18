package com.acon.server.member.infra.repository;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.member.infra.entity.VerifiedAreaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifiedAreaRepository extends JpaRepository<VerifiedAreaEntity, Long> {

    long countByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

    boolean existsByMemberIdAndName(Long memberId, String name);

    Optional<VerifiedAreaEntity> findById(Long id);

    Optional<VerifiedAreaEntity> findByMemberIdAndName(Long memberId, String name);

    default VerifiedAreaEntity findByIdOrElseThrow(Long id) {
        return findById(id).orElseThrow(
                () -> new BusinessException(ErrorType.NOT_FOUND_VERIFIED_AREA_ERROR)
        );
    }

    List<VerifiedAreaEntity> findAllByMemberId(Long memberId);
}
