package com.example.redissonlogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类。
 * 演示：登录后 token 写入 Redis（Redisson），
 * 即使重启本服务，只要 Redis 里的 token 没过期，登录态依然有效。
 */
@SpringBootApplication
public class RedissonLoginDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedissonLoginDemoApplication.class, args);
    }
}
