package com.wnis.linkyway.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisProvider {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public <T> T getData(String key, Class<T> classType) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            return getObjectData(valueOperations.get(key), classType);
        } catch (JsonProcessingException e) {
            log.error("", e);
            return null;
        }
    }

    public void setDataWithExpiration(String key, String value, long expireMills) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(expireMills);
        valueOperations.set(key, value, expireDuration);
    }

    public <T> void setDataWithExpiration(String key, T data, long expireMills) {
        try {
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            Duration expireDuration = Duration.ofMillis(expireMills);
            valueOperations.set(key, objectMapper.writeValueAsString(data), expireDuration);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    private <T> T getObjectData(String values, Class<T> classType) throws JsonProcessingException {
        if (!StringUtils.hasText(values)) {
            return null;
        }
        return objectMapper.readValue(values, classType);
    }

}
