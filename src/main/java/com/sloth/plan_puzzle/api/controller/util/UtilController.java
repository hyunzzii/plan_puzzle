package com.sloth.plan_puzzle.api.controller.util;

import com.sloth.plan_puzzle.api.service.util.UtilFacade;
import com.sloth.plan_puzzle.common.security.jwt.CustomUserDetails;
import com.sloth.plan_puzzle.dto.ListWrapperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/utils")
public class UtilController {
    private final UtilFacade utilFacade;

    @GetMapping("/images")
    public ListWrapperResponse<String> getPresignedUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam int number) {
        final Long userId = userDetails.getUserId();
        return ListWrapperResponse.of(utilFacade.getPresignedUrl(number, userId));
    }
}
