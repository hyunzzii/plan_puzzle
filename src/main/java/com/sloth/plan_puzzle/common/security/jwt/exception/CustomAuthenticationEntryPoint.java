package com.sloth.plan_puzzle.common.security.jwt.exception;

import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        CustomExceptionInfo exception = (CustomExceptionInfo) request.getAttribute("exception");
        response.setStatus(exception.getDetailStatusCode());
        response.getWriter().write(exception.getMessage());
    }
}
