package com.compadres.na.repository.user;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.compadres.na.model.user.PatchUserDetailsRq;
import com.compadres.na.model.user.PatchUserRq;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public String patchUser(PatchUserRq patchUserRq, String id) {

        return jdbcTemplate.queryForObject(
                UserQueryDB.PATCH_USER,
                PATCH_USER_MAPPER,
                patchUserParams(patchUserRq, UUID.fromString(id)));
    }

    @Override
    public String patchUserDetails(PatchUserDetailsRq createUserDetailsRq) {
        return jdbcTemplate.queryForObject(
                UserQueryDB.PATCH_USER_DETAILS,
                PATCH_USER_DETAILS_MAPPER,
                patchUserDetailsParams(createUserDetailsRq));
    }

    private static final RowMapper<String> PATCH_USER_MAPPER = (rs, rowNum) -> rs.getString("user_id");

    private static final RowMapper<String> PATCH_USER_DETAILS_MAPPER = (rs, rowNum) -> rs.getString("user_detail_id");

    private Object[] patchUserParams(PatchUserRq patchUserRq, UUID id) {
        return new Object[] {
                patchUserRq.email(),
                patchUserRq.password(),
                id };
    }

    private Object[] patchUserDetailsParams(PatchUserDetailsRq createUserDetailsRq) {
        return new Object[] {
                createUserDetailsRq.phone(),
                createUserDetailsRq.legalRepresentative(),
                UUID.fromString(createUserDetailsRq.userId()) };
    }

}
