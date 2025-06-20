package com.acon.server.member.api.response;

public record AppUpdateResponse(
        Boolean forceUpdateRequired
) {

    public static AppUpdateResponse of(boolean forceUpdateRequired) {
        return new AppUpdateResponse(forceUpdateRequired);
    }
}
