package com.sloth.plan_puzzle.api.service.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UtilFacade {
    private final AwsService awsService;

    public List<String> getPresignedUrl(final Integer number, final Long userId) {
        List<String> presignedUrls = new ArrayList<>();
        Date date = Date.from(Instant.now());
        String key = userId + "_" + date + "_"+ UUID.randomUUID().toString().substring(5);
        for (int i = 0; i < number; i++) {
            presignedUrls.add(awsService.generatePresignedUrl(key, date));
        }

        return presignedUrls;
    }
}
