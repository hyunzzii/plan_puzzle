package com.sloth.plan_puzzle.api.controller.auth;

import com.sloth.plan_puzzle.api.service.auth.AuthService;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.common.security.jwt.JwtUtil;
import com.sloth.plan_puzzle.dto.auth.JwtResponse;
import com.sloth.plan_puzzle.dto.auth.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest request) {
        return authService.generateTokenForLogin(request);
    }

    @GetMapping("/reissue")
    public JwtResponse reissueToken(@RequestHeader(JwtUtil.REFRESH_TOKEN_HEADER) String refreshToken) {
        return authService.generateTokenForReissue(refreshToken);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        final String loginId = userDetails.getUsername();
        authService.deleteExistingRefreshToken(loginId);
    }
}
