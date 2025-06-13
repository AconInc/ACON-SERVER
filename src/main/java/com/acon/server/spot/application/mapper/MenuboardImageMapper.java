package com.acon.server.spot.application.mapper;

import com.acon.server.spot.domain.entity.MenuboardImage;
import com.acon.server.spot.infra.entity.MenuboardImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MenuboardImageMapper {

    // MenuboardImageEntity -> MenuboardImage
    MenuboardImage toDomain(MenuboardImageEntity entity);

    // MenuboardImage -> MenuboardImageEntity
    MenuboardImageEntity toEntity(MenuboardImage domain);
}
