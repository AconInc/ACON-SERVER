package com.acon.server.spot.infra.repository;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.spot.infra.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByName(String name);

    default CategoryEntity findByNameOrElseThrow(String name) {
        return findByName(name).orElseThrow(
                () -> new BusinessException(ErrorType.INVALID_CATEGORY_NAME_ERROR)
        );
    }
}
