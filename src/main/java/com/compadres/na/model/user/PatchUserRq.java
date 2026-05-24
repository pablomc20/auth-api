package com.compadres.na.model.user;

import lombok.Builder;

@Builder
public record PatchUserRq(
        String email,
        String password,
        boolean enabled) {

}
