package com.sloth.plan_puzzle.common.security.jwt;

import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.EXPIRED_TOKEN;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.INVALID_TOKEN;
import static com.sloth.plan_puzzle.common.exception.CustomExceptionInfo.LOGOUT_TOKEN;
import static com.sloth.plan_puzzle.common.security.jwt.JwtUtil.AUTHORIZATION_HEADER;
import static com.sloth.plan_puzzle.common.security.jwt.JwtUtil.BEARER_PREFIX;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.security.sasl.AuthenticationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (authorization == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractAccessTokenFromAuthorization(authorization);

        Claims claims;
        try {
            claims = jwtUtil.parseClaimsFromToken(JwtType.ACCESS, accessToken);
        } catch (ExpiredJwtException expiredJwtException) {
            request.setAttribute("exception", EXPIRED_TOKEN);
            throw new AuthenticationException(EXPIRED_TOKEN.getMessage());
        } catch (Exception exception) {
            request.setAttribute("exception", INVALID_TOKEN);
            throw new AuthenticationException(INVALID_TOKEN.getMessage());
        }

        if (redisUtil.getValues(accessToken) != null && redisUtil.getValues(accessToken).equals("logout")) {
            request.setAttribute("exception", LOGOUT_TOKEN);
            throw new AuthenticationException(LOGOUT_TOKEN.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(new JwtAuthentication(claims, accessToken));
        filterChain.doFilter(request, response);
    }

    private String extractAccessTokenFromAuthorization(final String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(7);
        }
        return null;
    }
}
