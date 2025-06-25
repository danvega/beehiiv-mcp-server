package dev.danvega.beehiiv.core;

public final class ApiConstants {
    
    private ApiConstants() {
        // Utility class
    }
    
    // API Base Configuration
    public static final String BEEHIIV_API_BASE_URL = "https://api.beehiiv.com/v2";
    
    // Default Parameter Values
    public static final int DEFAULT_LIMIT = 10;
    public static final int MAX_LIMIT = 100;
    public static final int MIN_LIMIT = 1;
    public static final String DEFAULT_ORDER_BY = "publish_date";
    public static final String DEFAULT_DIRECTION = "desc";
    
    // API Endpoints
    public static final String POSTS_ENDPOINT = "/publications/{publicationId}/posts";
    public static final String SINGLE_POST_ENDPOINT = "/publications/{publicationId}/posts/{postId}";
    public static final String PUBLICATIONS_ENDPOINT = "/publications";
    public static final String SINGLE_PUBLICATION_ENDPOINT = "/publications/{publicationId}";
    
    // Expand Options
    public static final String EXPAND_FREE_EMAIL_CONTENT = "free_email_content";
    public static final String EXPAND_PREMIUM_EMAIL_CONTENT = "premium_email_content";
    public static final String EXPAND_FREE_WEB_CONTENT = "free_web_content";
    public static final String EXPAND_PREMIUM_WEB_CONTENT = "premium_web_content";
    public static final String EXPAND_FREE_RSS_CONTENT = "free_rss_content";
    public static final String EXPAND_STATS = "stats";
    
    // Default Expand Options
    public static final String[] DEFAULT_POST_EXPAND = {EXPAND_FREE_EMAIL_CONTENT, EXPAND_STATS};
    
    // Query Parameter Names
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_ORDER_BY = "order_by";
    public static final String PARAM_DIRECTION = "direction";
    public static final String PARAM_AUDIENCE = "audience";
    public static final String PARAM_PLATFORM = "platform";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_CONTENT_TAGS = "content_tags";
    public static final String PARAM_HIDDEN_FROM_FEED = "hidden_from_feed";
    public static final String PARAM_EXPAND = "expand[]";
    
    // Error Messages
    public static final String ERROR_INVALID_LIMIT = "Limit must be between 1 and 100";
    public static final String ERROR_INVALID_PAGE = "Page must be positive";
    public static final String ERROR_NULL_BLANK_POST_ID = "Post ID must not be null or blank";
    public static final String ERROR_NULL_BLANK_PUBLICATION_ID = "Publication ID must not be null or blank";
    public static final String ERROR_INVALID_PUBLICATION_ID_FORMAT = "Publication ID must start with 'pub_'";
    public static final String ERROR_NULL_BLANK_SEARCH_TERM = "Search term must not be null or blank";
    public static final String ERROR_NULL_BLANK_CONTENT_TAGS = "Content tags must not be null or blank";
}