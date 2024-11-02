package com.sloth.plan_puzzle.common.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {
    private final String loginId;
    private final Collection<? extends GrantedAuthority> roles;

    public CustomUserDetails(Claims claims){
        this.loginId = (String) claims.get(JwtUtil.CLAIM_USER_ID);
        this.roles = createGrantedAuthorities((List<String>) claims.get(JwtUtil.CLAIM_ROLES));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return loginId;
    }

    private Collection<? extends GrantedAuthority> createGrantedAuthorities(final List<String> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String role : roles) {
            grantedAuthorities.add(() -> role);
        }
        return grantedAuthorities;
    }
}
