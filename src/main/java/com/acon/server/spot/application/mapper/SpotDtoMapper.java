package com.acon.server.spot.application.mapper;

import com.acon.server.spot.api.response.SearchSuggestionResponse;
import com.acon.server.spot.api.response.SpotDetailResponse;
import com.acon.server.spot.infra.entity.SpotEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SpotDtoMapper {

    @Mapping(target = "spotId", source = "spotEntity.id")
    SpotDetailResponse toSpotDetailResponse(SpotEntity spotEntity, List<String> imageList, boolean openStatus);

    @Mapping(target = "spotId", source = "id")
        // TODO: 확인 요망
    SearchSuggestionResponse toSearchSuggestionResponse(SpotEntity spotEntity);
}
