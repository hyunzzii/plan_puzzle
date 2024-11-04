package com.sloth.plan_puzzle.api.service.auth;

import com.sloth.plan_puzzle.common.security.jwt.JwtType;
import com.sloth.plan_puzzle.common.security.jwt.JwtUtil;
import com.sloth.plan_puzzle.common.security.jwt.RedisUtil;
import com.sloth.plan_puzzle.domain.user.User;
import com.sloth.plan_puzzle.dto.auth.JwtResponse;
import com.sloth.plan_puzzle.dto.auth.LoginRequest;
import com.sloth.plan_puzzle.persistence.repository.user.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;

    public User validateUserByPassword(final LoginRequest userLoginRequest) {
        User user = User.fromEntity(userRepository.getUserByLoginId(userLoginRequest.loginId()));
        user.validatePassword(passwordEncoder, userLoginRequest.loginPw());
        return user;
    }

    public JwtResponse generateTokenForLogin(final LoginRequest userLoginRequest) {
        User user = validateUserByPassword(userLoginRequest);
        return createJwtTokens(user);
    }

    //refresh
    public JwtResponse generateTokenForReissue(final String refreshToken) {
        Long userId = jwtUtil.getUserIdFromToken(JwtType.REFRESH, refreshToken);
        User user = User.fromEntity(userRepository.getUserById(userId));    //해당 user 존재 검증

        redisUtil.validateRefreshToken(user.loginId(), refreshToken);
        deleteExistingRefreshToken(user.loginId());
        return createJwtTokens(user);
    }

    public void deleteExistingRefreshToken(final String loginId) {
        redisUtil.deleteByKey(loginId);
    }

    //private
    private JwtResponse createJwtTokens(final User user) {
        String accessToken = jwtUtil.createAccessToken(user.id(), user.loginId(),
                Collections.singletonList(user.role()));
        String refreshToken = jwtUtil.createRefreshToken(user.id());
        redisUtil.save(user.loginId(), refreshToken);
        return JwtResponse.builder()
                .grantType(JwtUtil.BEARER_PREFIX)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
