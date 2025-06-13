package com.acon.server.member.api.request;

import java.util.List;

public record PreferenceRequest(
        List<String> dislikeFoodList
) {

}
