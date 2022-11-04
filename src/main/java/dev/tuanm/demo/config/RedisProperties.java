package dev.tuanm.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
    private String host;
    private Integer port;
    private String password;
    private TimeToLive ttl;

    @Getter
    @Setter
    public static class TimeToLive {
        private Long defaultInSeconds;
        private Long postsView;
        private Long postsSearch;
    }
}
