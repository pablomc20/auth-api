package com.compadres.na.model.user;

import lombok.Builder;

@Builder
public record PatchUserDetailsRq(
        String userId,
        String phone,
        String legalRepresentative) {

}
