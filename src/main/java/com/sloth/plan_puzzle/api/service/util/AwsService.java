package com.sloth.plan_puzzle.api.service.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AwsService {

    private final AmazonS3 amazonS3;
    private static final String BUCKET_NAME = "planpuzzle-bucket";
    public static final Duration THREE_MINUTE = Duration.ofMinutes(3);

    public String generatePresignedUrl(String key, Date date) {
        GeneratePresignedUrlRequest presignedUrlRequest = new GeneratePresignedUrlRequest(BUCKET_NAME, key);
        presignedUrlRequest.setMethod(HttpMethod.PUT);
        presignedUrlRequest.setExpiration(Date.from(date.toInstant().plus(THREE_MINUTE)));
        return amazonS3.generatePresignedUrl(presignedUrlRequest).toExternalForm();
    }
}
