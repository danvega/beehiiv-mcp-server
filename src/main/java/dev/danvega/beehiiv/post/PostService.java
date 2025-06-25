package dev.danvega.beehiiv.post;

import dev.danvega.beehiiv.core.BeehiivProperties;
import dev.danvega.beehiiv.core.ApiException;
import dev.danvega.beehiiv.core.ApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public final class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final RestClient restClient;
    private final String defaultPublicationId;
    private final BeehiivProperties beehiivProperties;

    public PostService(RestClient restClient, BeehiivProperties beehiivProperties) {
        this.restClient = restClient;
        this.defaultPublicationId = beehiivProperties.defaultPublicationId();
        this.beehiivProperties = beehiivProperties;
        logger.info("Initializing PostService with default publication ID: {}", 
                defaultPublicationId != null ? defaultPublicationId : "none");
    }

    @Tool(name = "beehiiv_get_posts", description = "Get posts from Beehiiv newsletter with comprehensive filtering options.")
    public PostsResponse getAllPosts(
            @ToolParam(description = "Number of posts to return (1-100, default 10)", required = false) Integer limit,
            @ToolParam(description = "Page number for pagination (1+, default 1)", required = false) Integer page,
            @ToolParam(description = "Field to order by: publish_date, created, displayed_date (default: publish_date)", required = false) String orderBy,
            @ToolParam(description = "Sort direction: asc or desc (default: desc)", required = false) String sortOrder,
            @ToolParam(description = "Filter by audience: free, premium, all (default: all)", required = false) String audience,
            @ToolParam(description = "Filter by platform: web, email, both, all (default: all)", required = false) String platform,
            @ToolParam(description = "Filter by status: draft, confirmed, archived, all (default: all)", required = false) String status,
            @ToolParam(description = "Comma-separated list of content tags to filter by", required = false) String contentTags,
            @ToolParam(description = "Filter by hidden from feed: true, false, all (default: all)", required = false) String hiddenFromFeed,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        validatePaginationParams(limit, page);
        validateFilterParams(audience, platform, status, hiddenFromFeed);
        
        String resolvedPublicationId = resolvePublicationId(publicationId);
        String uri = buildPostsUri(resolvedPublicationId, limit, page, orderBy, sortOrder, audience, platform, status, contentTags, hiddenFromFeed);
        
        try {
            logger.info("Making request to Beehiiv API: {}", uri);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(PostsResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error calling Beehiiv API: {} - Status: {}, Body: {}", 
                    uri, e.getStatusCode(), e.getResponseBodyAsString());
            logger.debug("Full exception details: ", e);
            throw new ApiException("Error calling Beehiiv API: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Beehiiv API: {}", uri, e);
            throw new ApiException("Unexpected error calling Beehiiv API", e);
        }
    }

    @Tool(name = "beehiiv_get_post", description = "Get a single post from the Beehiiv Newsletter API by post ID.")
    public PostResponse getPostById(
            @ToolParam(description = "Post ID to retrieve (required)") String postId,
            @ToolParam(description = "Comma-separated expand options: free_email_content, premium_email_content, free_web_content, premium_web_content, free_rss_content, stats", required = false) String expand,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        if (postId == null || postId.isBlank()) {
            throw new IllegalArgumentException(ApiConstants.ERROR_NULL_BLANK_POST_ID);
        }
        
        try {
            String resolvedPublicationId = resolvePublicationId(publicationId);
            String uri = buildSinglePostUri(resolvedPublicationId, postId, expand);
            logger.info("Making request to Beehiiv API for post: {}", uri);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(PostResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error calling Beehiiv API for post {}: Status: {}, Body: {}", 
                    postId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.debug("Full exception details: ", e);
            throw new ApiException("Error retrieving post with ID " + postId, e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Beehiiv API for post: {}", postId, e);
            throw new ApiException("Unexpected error retrieving post with ID " + postId, e);
        }
    }

    /**
     * Helper method to build the URI for posts endpoint with query parameters
     */
    private String buildPostsUri(String publicationId, Integer limit, Integer page, String orderBy, String sortOrder,
                                String audience, String platform, String status, 
                                String contentTags, String hiddenFromFeed) {
        String uri = "/publications/" + publicationId + "/posts";
        StringBuilder queryParams = new StringBuilder("?");
        boolean hasParam = false;
        
        // Add parameters if they are provided
        if (limit != null) {
            queryParams.append("limit=").append(limit);
            hasParam = true;
        }
        
        if (page != null) {
            if (hasParam) queryParams.append("&");
            queryParams.append("page=").append(page);
            hasParam = true;
        }
        
        // Add ordering parameter with default
        if (hasParam) queryParams.append("&");
        queryParams.append("order_by=");
        if (orderBy != null) {
            queryParams.append(orderBy);
        } else {
            queryParams.append("publish_date");
        }
        
        // Add direction parameter with default
        queryParams.append("&direction=");
        if (sortOrder != null) {
            queryParams.append(sortOrder);
        } else {
            queryParams.append("desc");
        }
        
        // Add audience filter
        if (audience != null && !"all".equalsIgnoreCase(audience)) {
            queryParams.append("&audience=").append(audience);
        }
        
        // Add platform filter
        if (platform != null && !"all".equalsIgnoreCase(platform)) {
            queryParams.append("&platform=").append(platform);
        }
        
        // Add status filter
        if (status != null && !"all".equalsIgnoreCase(status)) {
            queryParams.append("&status=").append(status);
        }
        
        // Add content tags filter
        if (contentTags != null && !contentTags.isBlank()) {
            String[] tags = contentTags.split(",");
            for (String tag : tags) {
                queryParams.append("&content_tags=").append(tag.trim());
            }
        }
        
        // Add hidden from feed filter
        if (hiddenFromFeed != null && !"all".equalsIgnoreCase(hiddenFromFeed)) {
            queryParams.append("&hidden_from_feed=").append(hiddenFromFeed);
        }
        
        return uri + queryParams.toString();
    }
    
    /**
     * Validates pagination parameters
     */
    private void validatePaginationParams(Integer limit, Integer page) {
        if (limit != null && (limit < ApiConstants.MIN_LIMIT || limit > ApiConstants.MAX_LIMIT)) {
            throw new IllegalArgumentException(ApiConstants.ERROR_INVALID_LIMIT);
        }
        
        if (page != null && page < 1) {
            throw new IllegalArgumentException(ApiConstants.ERROR_INVALID_PAGE);
        }
    }
    
    /**
     * Validates filter parameters using enums
     */
    private void validateFilterParams(String audience, String platform, String status, String hiddenFromFeed) {
        if (audience != null) {
            PostAudience.fromString(audience);
        }
        
        if (platform != null) {
            PostPlatform.fromString(platform);
        }
        
        if (status != null) {
            PostStatus.fromString(status);
        }
        
        if (hiddenFromFeed != null) {
            HiddenFromFeed.fromString(hiddenFromFeed);
        }
    }
    
    /**
     * Fetches all pages of posts with pagination handling
     * @param limit Maximum number of posts per page
     * @param orderBy Field to order by
     * @param sortOrder Sort direction
     * @param audience Audience filter
     * @param platform Platform filter
     * @param status Status filter
     * @param contentTags Content tags filter
     * @param hiddenFromFeed Hidden from feed filter
     * @return Combined list of all posts from all pages
     */
    public List<Post> getAllPostsWithPagination(Integer limit, String orderBy, String sortOrder,
                                              String audience, String platform, String status,
                                              String contentTags, String hiddenFromFeed, String publicationId) {
        List<Post> allPosts = new ArrayList<>();
        int currentPage = 1;
        PostsResponse response;
        
        do {
            response = getAllPosts(limit, currentPage, orderBy, sortOrder, audience, platform, status, contentTags, hiddenFromFeed, publicationId);
            if (response != null && response.data() != null) {
                allPosts.addAll(response.data());
            }
            currentPage++;
        } while (response != null && response.pagination() != null && response.pagination().nextPage() != null);
        
        return allPosts;
    }
    
    /**
     * Convenience method for getting posts by status
     */
    @Tool(name = "beehiiv_get_posts_by_status", description = "Get posts filtered by status.")
    public PostsResponse getPostsByStatus(
            @ToolParam(description = "Post status: draft (not scheduled), confirmed (scheduled/published), archived (no longer active)") String status,
            @ToolParam(description = "Number of posts to return (1-100, default 10)", required = false) Integer limit,
            @ToolParam(description = "Page number for pagination (1+, default 1)", required = false) Integer page,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        validatePaginationParams(limit, page);
        PostStatus.fromString(status); // Validate status
        return getAllPosts(limit, page, null, null, null, null, status, null, null, publicationId);
    }
    
    /**
     * Convenience method for getting posts by platform
     */
    @Tool(name = "beehiiv_get_posts_by_platform", description = "Get posts filtered by platform.")
    public PostsResponse getPostsByPlatform(
            @ToolParam(description = "Platform filter: web (web only), email (email only), both (email and web)") String platform,
            @ToolParam(description = "Number of posts to return (1-100, default 10)", required = false) Integer limit,
            @ToolParam(description = "Page number for pagination (1+, default 1)", required = false) Integer page,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        validatePaginationParams(limit, page);
        PostPlatform.fromString(platform); // Validate platform
        return getAllPosts(limit, page, null, null, null, platform, null, null, null, publicationId);
    }
    
    /**
     * Convenience method for getting posts by content tags
     */
    @Tool(name = "beehiiv_get_posts_by_tags", description = "Get posts filtered by content tags.")
    public PostsResponse getPostsByTags(
            @ToolParam(description = "Comma-separated tag names to filter posts containing those tags") String contentTags,
            @ToolParam(description = "Number of posts to return (1-100, default 10)", required = false) Integer limit,
            @ToolParam(description = "Page number for pagination (1+, default 1)", required = false) Integer page,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        validatePaginationParams(limit, page);
        if (contentTags == null || contentTags.isBlank()) {
            throw new IllegalArgumentException(ApiConstants.ERROR_NULL_BLANK_CONTENT_TAGS);
        }
        return getAllPosts(limit, page, null, null, null, null, null, contentTags, null, publicationId);
    }
    
    /**
     * Helper method to resolve the publication ID to use
     * @param providedPublicationId Publication ID provided by the user (optional)
     * @return Publication ID to use for the API call
     * @throws IllegalArgumentException if no publication ID is available
     */
    private String resolvePublicationId(String providedPublicationId) {
        if (providedPublicationId != null && !providedPublicationId.isBlank()) {
            // Validate provided publication ID format
            if (!providedPublicationId.startsWith("pub_")) {
                throw new IllegalArgumentException("Publication ID must start with 'pub_'");
            }
            return providedPublicationId;
        }
        
        // Use default publication ID if available
        if (beehiivProperties.hasDefaultPublicationId()) {
            return defaultPublicationId;
        }
        
        // No publication ID available
        throw new IllegalArgumentException("No publication ID provided and no default publication ID configured. " +
                "Please provide a publication ID or configure BEEHIIV_PUBLICATION_ID environment variable.");
    }
    
    /**
     * Helper method to build URI for single post with expand options
     */
    private String buildSinglePostUri(String publicationId, String postId, String expand) {
        StringBuilder uri = new StringBuilder("/publications/" + publicationId + "/posts/" + postId);
        
        if (expand != null && !expand.isBlank()) {
            uri.append("?");
            String[] expandOptions = expand.split(",");
            boolean first = true;
            
            for (String option : expandOptions) {
                String trimmedOption = option.trim();
                if (!trimmedOption.isEmpty()) {
                    if (!first) {
                        uri.append("&");
                    }
                    uri.append("expand[]=").append(trimmedOption);
                    first = false;
                }
            }
        } else {
            // Default expand options for better data retrieval
            uri.append("?expand[]=free_email_content&expand[]=stats");
        }
        
        return uri.toString();
    }
    
}
