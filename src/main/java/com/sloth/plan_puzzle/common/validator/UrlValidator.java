package com.sloth.plan_puzzle.common.validator;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidator {
    public static final String EXPECTED_PROTOCOL = "https";
    public static final String BUCKET_PREFIX = "planpuzzle-bucket.s3";
    public static final String AWS_HOST_SUFFIX = "amazonaws.com";

    public static void isValidateUrl(final String url) {
        boolean validation;
        try {
            URL parsedUrl = new URL(url);
            validation = EXPECTED_PROTOCOL.equals(parsedUrl.getProtocol()) &&
                    parsedUrl.getHost().startsWith(BUCKET_PREFIX) &&
                    parsedUrl.getHost().endsWith(AWS_HOST_SUFFIX);
        } catch (MalformedURLException e) {
            throw new CustomException(CustomExceptionInfo.INVALID_IMG_URL);
        }
        if (!validation) {
            throw new CustomException(CustomExceptionInfo.INVALID_IMG_URL);
        }
    }
}
