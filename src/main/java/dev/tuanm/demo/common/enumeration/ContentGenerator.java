package dev.tuanm.demo.common.enumeration;

public enum ContentGenerator {
    DEFAULT("default"),
    LOREM_IPSUM("lorem-ipsum");

    private final String name;

    private ContentGenerator(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
