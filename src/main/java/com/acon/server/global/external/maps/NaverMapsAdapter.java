package com.acon.server.global.external.maps;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NaverMapsAdapter {

    private final NaverMapsClient naverMapsClient;

    public GeoCodingResponse getGeoCodingResult(String address) {
        // TODO: try-catch로 감싸기
        return Optional.ofNullable(naverMapsClient.getGeoCode(address))
                .map(response -> (List<Map<String, Object>>) response.get("addresses"))
                .filter(addresses -> !addresses.isEmpty())
                .map(addresses -> addresses.get(0))
                .map(firstAddress -> new GeoCodingResponse(
                        (String) firstAddress.get("y"),
                        (String) firstAddress.get("x")
                ))
                .orElseThrow(
                        () -> new BusinessException(ErrorType.NAVER_MAPS_GEOCODING_API_ERROR));
    }

    // TODO: 코드 정리하기, 슬래시 디코딩 방지
    public String getReverseGeoCodingResult(final Double latitude, final Double longitude) {
        Map<String, Object> response = naverMapsClient.getReverseGeocode(longitude + "," + latitude, "legalcode",
                "json");

        try {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            if (results != null && !results.isEmpty()) {
                Map<String, Object> region = (Map<String, Object>) results.get(0).get("region");
                if (region != null) {
                    Map<String, Object> area3 = (Map<String, Object>) region.get("area3");
                    if (area3 != null) {
                        return (String) area3.get("name");
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorType.INTERNAL_SERVER_ERROR);
        }
        throw new BusinessException(ErrorType.INTERNAL_SERVER_ERROR);
    }
}
