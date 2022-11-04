package dev.tuanm.demo.service.factory;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import dev.tuanm.demo.common.constant.ContentGeneratingConstants;
import dev.tuanm.demo.common.enumeration.ContentGenerator;
import dev.tuanm.demo.service.ContentGeneratingService;

@Service
public class ContentGeneratingServiceFactory {

    private final ContentGeneratingService defaultContentGeneratingService;
    private final ContentGeneratingService loremIpsumContentGeneratingService;

    public ContentGeneratingServiceFactory(
            @Qualifier(ContentGeneratingConstants.DEFAULT_GENERATOR) ContentGeneratingService defaContentGeneratingService,
            @Qualifier(ContentGeneratingConstants.LOREM_IPSUM_GENERATOR) ContentGeneratingService loremIpsumContentGeneratingService) {
        this.defaultContentGeneratingService = defaContentGeneratingService;
        this.loremIpsumContentGeneratingService = loremIpsumContentGeneratingService;
    }

    /**
     * Retrieves a service for content generating feature.
     *
     * @param generator the content generator's name.
     */
    public ContentGeneratingService getService(String generator) {
        ContentGenerator contentGenerator = Arrays.asList(ContentGenerator.values())
                .stream()
                .filter(cg -> cg.getName().equals(generator))
                .findAny()
                .orElse(ContentGenerator.DEFAULT);

        switch (contentGenerator) {
            case LOREM_IPSUM:
                return loremIpsumContentGeneratingService;
            case DEFAULT:
                return defaultContentGeneratingService;
            default:
                return defaultContentGeneratingService;
        }
    }
}
