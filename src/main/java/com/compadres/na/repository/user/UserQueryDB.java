package com.compadres.na.repository.user;

public class UserQueryDB {
    private UserQueryDB() {
	}

    public static final String PATCH_USER = """
			UPDATE users
			SET email = COALESCE(?, email),
				password = COALESCE(?, password),
			    updated_at = NOW(),
                enabled = TRUE
			WHERE user_id = ? RETURNING user_id
			""";

	public static final String PATCH_USER_DETAILS = """
			UPDATE user_details
			SET phone = COALESCE(?, phone),
			    legal_representative = COALESCE(?, legal_representative)
			WHERE user_id = ? RETURNING user_detail_id
			""";
}
