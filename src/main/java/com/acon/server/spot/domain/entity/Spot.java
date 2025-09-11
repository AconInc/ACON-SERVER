package com.acon.server.spot.domain.entity;

import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.domain.enums.SpotType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Getter
public class Spot {

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    private final Long id;
    private final String name;
    private final SpotType spotType;
    private final String address;
    private final Long appliedMemberId;

    private Integer localAcornCount;
    private Integer basicAcornCount;
    private Double latitude;
    private Double longitude;
    private Point geom;
    private String legalDong;
    private SpotStatus spotStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Spot(
            Long id,
            String name,
            SpotType spotType,
            String address,
            Long appliedMemberId,
            Integer localAcornCount,
            Integer basicAcornCount,
            Double latitude,
            Double longitude,
            Point geom,
            String legalDong,
            SpotStatus spotStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.name = name;
        this.spotType = spotType;
        this.address = address;
        this.appliedMemberId = appliedMemberId;
        this.localAcornCount = localAcornCount;
        this.basicAcornCount = basicAcornCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.geom = geom;
        this.legalDong = legalDong;
        this.spotStatus = spotStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void addAcorn(int acornCount, boolean isLocal) {
        if (isLocal) {
            addLocalAcorn(acornCount);
        } else {
            addBasicAcorn(acornCount);
        }
    }

    private void addLocalAcorn(int acornCount) {
        this.localAcornCount += acornCount;
    }

    private void addBasicAcorn(int acornCount) {
        this.basicAcornCount += acornCount;
    }

    public void updateCoordinate(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateGeom() {
        if (latitude != null && longitude != null) {
            this.geom = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            this.geom.setSRID(4326);
        }
    }

    public void updateLegalDong(String legalDong) {
        if (latitude != null && longitude != null) {
            this.legalDong = legalDong;
        }
    }
}
