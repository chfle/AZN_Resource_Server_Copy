package com.lokcenter.AZN_Spring_ResourceServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis configuration file
 *
 * @version 1.02 - 01-07-2022
 */
public class redisConfig {
    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<?, ?> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        return template;
    }
}
