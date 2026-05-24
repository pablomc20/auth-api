package com.compadres.na.repository.user;

import com.compadres.na.model.user.PatchUserDetailsRq;
import com.compadres.na.model.user.PatchUserRq;

public interface UserRepository {
    String patchUser(PatchUserRq patchUserRq, String id);

    String patchUserDetails(PatchUserDetailsRq createUserDetailsRq);
}
