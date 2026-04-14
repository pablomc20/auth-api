package com.compadres.na.repository.auth;

public final class UserQueryBD {

    private UserQueryBD(){

    }

    public static final String GET_USER_BY_EMAIL = """
            SELECT u FROM User u " +
            "LEFT JOIN FETCH u.userDetail " +
            "WHERE u.email = :email
            """;
}
