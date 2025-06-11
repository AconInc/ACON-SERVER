package com.acon.server.member.application.mapper;

import com.acon.server.member.domain.entity.SavedSpot;
import com.acon.server.member.infra.entity.SavedSpotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SavedSpotMapper {

    // SavedSpotEntity -> SavedSpot
    SavedSpot toDomain(SavedSpotEntity entity);

    // SavedSpot -> SavedSpotEntity
    SavedSpotEntity toEntity(SavedSpot domain);
}
