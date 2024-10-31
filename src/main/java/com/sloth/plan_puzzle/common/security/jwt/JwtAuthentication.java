package com.sloth.plan_puzzle.common.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthentication implements Authentication {

    private final String credentials;
    private final String principal;
    private final Collection<? extends GrantedAuthority> authorities;     //role 정보를 담은 GrantedAuthority 리스트
    private boolean authenticated;
    //    private Object details;
    //    public JwtAuthentication(final String userId, final String credentials,
//                             final Collection<? extends GrantedAuthority> authorities) {
//        this.principal = userId;
//        this.authorities = authorities;
//        this.credentials = credentials;
//        this.authenticated = true;
//    }

    public JwtAuthentication(final Claims claims, final String credentials) {
        this.principal = (String) claims.get(JwtUtil.CLAIM_USER_ID);
        this.authorities = createGrantedAuthorities((List<String>) claims.get(JwtUtil.CLAIM_ROLES));
        this.credentials = credentials;
        this.authenticated = true;
    }

    private Collection<? extends GrantedAuthority> createGrantedAuthorities(final List<String> roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String role : roles) {
            grantedAuthorities.add(() -> role);
        }
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return null;
    }
}
