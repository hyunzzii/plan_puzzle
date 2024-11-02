package com.sloth.plan_puzzle.common.security.jwt;

import static com.sloth.plan_puzzle.common.security.jwt.JwtUtil.REFRESH_DURATION;

import com.sloth.plan_puzzle.common.exception.CustomException;
import com.sloth.plan_puzzle.common.exception.CustomExceptionInfo;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(final String key, final String value) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, Duration.ofMillis(REFRESH_DURATION));
    }

    public String getValues(final String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String value = values.get(key);
        if (value == null) {
            throw new CustomException(CustomExceptionInfo.INVALID_REFRESH_TOKEN);
        }
        return value;
    }

    public void validateRefreshToken(final String key, final String refreshToken){
        if(!getValues(key).equals(refreshToken)){
            throw new CustomException(CustomExceptionInfo.INVALID_REFRESH_TOKEN);
        }
    }

    public void deleteByKey(final String key) {
        redisTemplate.delete(key);
    }
}
