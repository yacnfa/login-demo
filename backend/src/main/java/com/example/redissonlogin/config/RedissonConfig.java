package com.example.redissonlogin.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置：把 Redis 连接交给 RedissonClient 管理。
 * 配置读取 spring.data.redis.*（Spring Boot 标准前缀）。
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = "redis://" + host + ":" + port;
        if (password != null && !password.isBlank()) {
            config.useSingleServer()
                    .setAddress(address)
                    .setPassword(password);
        } else {
            config.useSingleServer()
                    .setAddress(address);
        }
        return Redisson.create(config);
    }
}
