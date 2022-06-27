package com.wnis.linkyway.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstants {

    public static final String LOGIN_KEY_PREFIX = "LG-";
    public static final int LOGIN_RESTRICT_COUNT = 5;
    public static final long LOGIN_RESTRICT_TIMEOUT_MILLS = 1000 * 60 * 5L;

    public static final long EMAIL_VALIDATION_EXPIRATION_TIME = 1000 * 60 * 15L;
    public static final int EMAIL_VALIDATION_RETRY_COUNT = 5;

}
