package dev.tuanm.demo.model.response;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import lombok.Builder;
import lombok.Data;

@RedisHash("PostInfoResponse")
@Data
@Builder
public class PostInfoResponse implements Serializable {
    private Long id;
    private String title;
    private String content;
    private String author;
}
