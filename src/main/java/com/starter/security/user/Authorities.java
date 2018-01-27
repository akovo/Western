package com.calewiz.security.user;

import com.calewiz.security.UserRole;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class Authorities {

    private static final int AUTHORITIES_SIZE = 3;
    private static final String ROLE_PREFIX = "ROLE_";

    public static final String USER_ID_KEY = "user-id";
    public static final String ORGANIZATION_ID_KEY = "organization-id";
    public static final String ROLE_KEY = "role";

    public static Map<String, Object> getPropertiesFromGrantedAuthorities(Set<GrantedAuthority> authorities) {
        val filtered = authorities.stream().map(x -> x.getAuthority().split(":")).collect(toList());

        val organizationId = filtered.stream().filter(x -> x.length == 2 && x[0].equals(ORGANIZATION_ID_KEY) && StringUtils.isNumeric(x[1]))
                .map(x -> Long.valueOf(x[1]))
                .findFirst().orElseThrow(() -> new IllegalStateException("Invalid organization id!"));

        val userId = filtered.stream().filter(x -> x.length == 2 && x[0].equals(USER_ID_KEY) && StringUtils.isNumeric(x[1]))
                .map(x -> Long.valueOf(x[1]))
                .findFirst().orElseThrow(() -> new IllegalStateException("Invalid user id!"));

        val role = filtered.stream().filter(x -> x.length == 1 && x[0].startsWith(ROLE_PREFIX))
                .map(x -> UserRole.fromRoleName(x[0]))
                .findFirst().orElseThrow(() -> new IllegalStateException("Invalid role!"));

        return new ImmutableMap.Builder<String, Object>()
                .put(USER_ID_KEY, userId)
                .put(ORGANIZATION_ID_KEY, organizationId)
                .put(ROLE_KEY, role)
                .build();
    }

    public static Set<GrantedAuthority> generateGrantedAuthorities(Long userId, Long organizationId, UserRole role) {
        val map = new HashSet<GrantedAuthority>();
        map.add(new SimpleGrantedAuthority(USER_ID_KEY + ":" + userId));
        map.add(new SimpleGrantedAuthority(ORGANIZATION_ID_KEY + ":" + organizationId));
        map.add(new SimpleGrantedAuthority(role.getRoleName()));
        return map;
    }

    public static boolean validAuthorities(Set<GrantedAuthority> authorities) {
        if (authorities.size() != AUTHORITIES_SIZE) {
            return false;
        }

        val filtered = authorities.stream().map(x -> x.getAuthority().split(":")).collect(toList());

        val containsOrgnaizationId = filtered.stream().filter(x -> x.length == 2 && x[0].equals(ORGANIZATION_ID_KEY) && StringUtils.isNumeric(x[1])).count() == 1;
        val containsUserId = filtered.stream().filter(x -> x.length == 2 && x[0].equals(USER_ID_KEY) && StringUtils.isNumeric(x[1])).count() == 1;
        val containsRole = filtered.stream().filter(x -> x.length == 1 && x[0].startsWith(ROLE_PREFIX)  && UserRole.fromRoleName(x[0]) != null).count() == 1;

        return containsOrgnaizationId && containsUserId && containsRole;
    }

}
