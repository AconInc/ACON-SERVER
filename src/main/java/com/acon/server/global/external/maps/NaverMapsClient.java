package com.acon.server.global.external.maps;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "naverMapsClient",
        url = "https://maps.apigw.ntruss.com",
        configuration = NaverMapsFeignConfig.class
)
public interface NaverMapsClient {

    @GetMapping(path = "/map-geocode/v2/geocode")
    Map<String, Object> getGeoCode(
            @RequestParam("query") String query
    );

    @GetMapping(path = "/map-reversegeocode/v2/gc")
    Map<String, Object> getReverseGeocode(
            @RequestParam("coords") String coords,
            @RequestParam("orders") String orders,
            @RequestParam("output") String output
    );
}
