package com.calewiz.security.user;

import com.calewiz.models.entities.User;
import com.calewiz.models.entities.enumerations.UserStatus;
import com.calewiz.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

public class CustomUserPrincipal implements UserDetails {

    private User user;
    private Authorities authorities;

    CustomUserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return Authorities.generateGrantedAuthorities(user.getId(), user.getOrganization().getId(),
                UserRole.fromUserStatusAndType(user.getUserStatus(), user.getUserType()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return user.getUserStatus() == UserStatus.ACTIVE;
    }

    public Long getOrganizationId() {
        return user.getOrganization().getId();
    }
}
