package com.sloth.plan_puzzle.api.controller.user;

import com.sloth.plan_puzzle.api.service.user.UserService;
import com.sloth.plan_puzzle.dto.user.UserSignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/validation/login-id")
    public void validateLoginId(@RequestParam String loginId) {
        userService.isDuplicateLoginId(loginId);
    }


    @PostMapping
    public void userSignUp(@RequestBody @Valid UserSignupRequest request) {
        userService.createUser(request);
    }
}
