package dev.tuanm.demo.common.constant;

@SuppressWarnings("java:S2386")
public final class PathConstants {
    private PathConstants() {
    }

    public static final String ALL_PATTERN = "/**";
    public static final String API_BASE_URL = "/api";
    public static final String PRIVATE_BASE_URL = "/private";
    public static final String API_ADMIN_BASE_URL = API_BASE_URL + "/admin";
    public static final String JWT_AUTH_URL_PATTERN = API_BASE_URL + ALL_PATTERN;
    public static final String PRIVATE_AUTH_URL_PATTERN = PRIVATE_BASE_URL + ALL_PATTERN;

    public static final String API_AUTHENTICATION_URL = API_BASE_URL + "/authenticate";
    public static final String API_REGISTRATION_URL = API_BASE_URL + "/register";

    public static final String API_POSTS_BASE_URL = API_BASE_URL + "/posts";
    public static final String API_POSTS_CREATION_URL = API_POSTS_BASE_URL + "";
    public static final String API_POSTS_SEARCH_URL = API_POSTS_BASE_URL + "";
    public static final String API_POSTS_VIEW_URL = API_POSTS_BASE_URL + "/{postId}";
    public static final String API_POSTS_DELETION_URL = API_POSTS_BASE_URL + "/{postId}";
    public static final String API_POSTS_UPLOAD_URL = API_POSTS_BASE_URL + "/upload";
    public static final String API_POSTS_GENERATE_URL = API_POSTS_BASE_URL + "/generate";

    public static final String API_USERS_BASE_URL = API_BASE_URL + "/users";
    public static final String API_USERS_INFO_URL = API_USERS_BASE_URL + "";

    public static final String PRIVATE_TEST_URL = PRIVATE_BASE_URL + "/test";

    public static final String API_ADMIN_USERS_INFO_URL = API_ADMIN_BASE_URL + "/users/{username}";

    public static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // -- Public APIs
            API_AUTHENTICATION_URL, // login
            API_REGISTRATION_URL, // register
            "/public/**", // public resource (html, js, css, images, etc.)
            "/test/**" // testing
    };

    public static final String[] ADMIN_AUTH_LIST = {
            API_ADMIN_BASE_URL + ALL_PATTERN
    };
}
