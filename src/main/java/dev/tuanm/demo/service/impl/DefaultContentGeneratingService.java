package dev.tuanm.demo.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import dev.tuanm.demo.common.constant.ContentGeneratingConstants;
import dev.tuanm.demo.service.ContentGeneratingService;

@Service(ContentGeneratingConstants.DEFAULT_GENERATOR)
public class DefaultContentGeneratingService implements ContentGeneratingService {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
