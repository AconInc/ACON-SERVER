package com.acon.server.spot.infra.repository;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.spot.infra.entity.OptionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {

    Optional<OptionEntity> findByCategoryIdAndName(Long categoryId, String name);

    default OptionEntity findByCategoryIdAndNameOrElseThrow(Long categoryId, String name) {
        return findByCategoryIdAndName(categoryId, name).orElseThrow(
                () -> new BusinessException(ErrorType.INVALID_CATEGORY_OPTION_ERROR)
        );
    }
}
