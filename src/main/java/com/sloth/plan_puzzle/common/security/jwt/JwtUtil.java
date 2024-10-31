package com.sloth.plan_puzzle.common.security.jwt;

import com.sloth.plan_puzzle.domain.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
//@PropertySource("classpath:application.yml")
public class JwtUtil {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_USER_ID = "userId";

    public static final Long ACCESS_DURATION = 30 * 1000 * 60L;
    public static final Long REFRESH_DURATION = 240 * 1000 * 60L;

    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public JwtUtil(@Value("${jwt.access-secret}") String accessSecretKey,
                   @Value("${jwt.refresh-secret}") String refreshSecretKey) {
        this.accessSecret = Keys.hmacShaKeyFor(
                Base64.getEncoder().encodeToString(accessSecretKey.getBytes()).getBytes());
        this.refreshSecret = Keys.hmacShaKeyFor(
                Base64.getEncoder().encodeToString(refreshSecretKey.getBytes()).getBytes());
    }

    public String createAccessToken(final String userId, final List<UserRole> roles) {
        return Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLES, roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_DURATION))
                .signWith(accessSecret)
                .compact();
    }

    public String createRefreshToken(final String userId) {
        return Jwts.builder()
                .claim(CLAIM_USER_ID, userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_DURATION))
                .signWith(refreshSecret)
                .compact();
    }

    public Claims parseClaimsFromToken(final JwtType jwtType, final String token) {
        SecretKey secretForSign = jwtType == JwtType.ACCESS ? accessSecret : refreshSecret;
        return Jwts.parser()
                .verifyWith(secretForSign)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Date getExpiration(final JwtType jwtType, final String token){
        return parseClaimsFromToken(jwtType, token).getExpiration();
    }
}

