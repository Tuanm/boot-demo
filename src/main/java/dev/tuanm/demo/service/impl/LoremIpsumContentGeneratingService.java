package dev.tuanm.demo.service.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.tuanm.demo.common.constant.ContentGeneratingConstants;
import dev.tuanm.demo.service.ContentGeneratingService;
import lombok.Getter;
import lombok.Setter;

@Service(ContentGeneratingConstants.LOREM_IPSUM_GENERATOR)
public class LoremIpsumContentGeneratingService implements ContentGeneratingService {

    private final LoremIpsumContentGeneratorProperties properties;
    private final RestTemplate restTemplate;

    public LoremIpsumContentGeneratingService(LoremIpsumContentGeneratorProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    @Configuration
    @ConfigurationProperties(prefix = "api.posts.generator.lorem-ipsum")
    @Setter
    @Getter
    public static class LoremIpsumContentGeneratorProperties {
        private String url;
    }

    @Override
    public String generate() {
        return restTemplate.getForObject(properties.getUrl(), String.class);
    }
}
