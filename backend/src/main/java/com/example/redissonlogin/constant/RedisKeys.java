package com.example.redissonlogin.constant;

/** Redis key 常量 */
public final class RedisKeys {

    /** token -> 用户信息(JSON)。例如 auth:token:abc123 */
    public static final String TOKEN_KEY_PREFIX = "auth:token:";

    private RedisKeys() {}
}
