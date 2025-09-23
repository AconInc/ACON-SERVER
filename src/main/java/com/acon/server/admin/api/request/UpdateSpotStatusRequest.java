package com.acon.server.admin.api.request;

import com.acon.server.spot.domain.enums.SpotStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSpotStatusRequest(
        @NotNull(message = "targetStatus는 필수입니다.")
        SpotStatus targetStatus
) {

}
