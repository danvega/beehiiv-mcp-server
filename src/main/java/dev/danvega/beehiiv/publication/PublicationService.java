package dev.danvega.beehiiv.publication;

import dev.danvega.beehiiv.core.ApiException;
import dev.danvega.beehiiv.core.BeehiivProperties;
import dev.danvega.beehiiv.core.ApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public final class PublicationService {

    private static final Logger logger = LoggerFactory.getLogger(PublicationService.class);
    private final RestClient restClient;
    private final String defaultPublicationId;
    private final BeehiivProperties beehiivProperties;

    public PublicationService(RestClient restClient, BeehiivProperties beehiivProperties) {
        this.restClient = restClient;
        this.defaultPublicationId = beehiivProperties.defaultPublicationId();
        this.beehiivProperties = beehiivProperties;
        logger.info("Initializing PublicationService with default publication ID: {}", 
                defaultPublicationId != null ? defaultPublicationId : "none");
    }

    @Tool(name = "beehiiv_get_publications", description = "Get a list of all publications with filtering options.")
    public PublicationsResponse getAllPublications(
            @ToolParam(description = "Number of publications to return (1-100, default 10)", required = false) Integer limit,
            @ToolParam(description = "Page number for pagination (1+, default 1)", required = false) Integer page,
            @ToolParam(description = "Field to order by: created, name", required = false) String orderBy,
            @ToolParam(description = "Sort direction: asc or desc", required = false) String direction) {
        validatePaginationParams(limit, page);
        
        String uri = buildPublicationsUri(limit, page, orderBy, direction);
        
        try {
            logger.info("Making request to Beehiiv API: {}", uri);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(PublicationsResponse.class);
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

    @Tool(name = "beehiiv_get_publication", description = "Get detailed information about a specific publication by ID.")
    public PublicationResponse getPublicationById(
            @ToolParam(description = "Publication ID (required - must start with 'pub_')") String publicationId) {
        if (publicationId == null || publicationId.isBlank()) {
            throw new IllegalArgumentException(ApiConstants.ERROR_NULL_BLANK_PUBLICATION_ID);
        }
        
        if (!publicationId.startsWith("pub_")) {
            throw new IllegalArgumentException(ApiConstants.ERROR_INVALID_PUBLICATION_ID_FORMAT);
        }
        
        try {
            String uri = "/publications/" + publicationId;
            logger.info("Making request to Beehiiv API for publication: {}", uri);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(PublicationResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("Error calling Beehiiv API for publication {}: Status: {}, Body: {}", 
                    publicationId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.debug("Full exception details: ", e);
            throw new ApiException("Error retrieving publication with ID " + publicationId, e);
        } catch (Exception e) {
            logger.error("Unexpected error calling Beehiiv API for publication: {}", publicationId, e);
            throw new ApiException("Unexpected error retrieving publication with ID " + publicationId, e);
        }
    }

    /**
     * Helper method to build the URI for publications endpoint with query parameters
     */
    private String buildPublicationsUri(Integer limit, Integer page, String orderBy, String direction) {
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
        if (orderBy != null) {
            if (hasParam) queryParams.append("&");
            queryParams.append("order_by=").append(orderBy);
            hasParam = true;
        }
        
        // Add direction parameter with default
        if (direction != null) {
            if (hasParam) queryParams.append("&");
            queryParams.append("direction=").append(direction);
        }
        
        String uri = "/publications";
        if (hasParam) {
            uri += queryParams.toString();
        }
        
        return uri;
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
     * Returns all publications without pagination in a single list
     */
    public List<Publication> getAllPublicationsWithPagination(Integer limit, String orderBy, String direction) {
        PublicationsResponse response = getAllPublications(limit, 1, orderBy, direction);
        
        if (response == null || response.data() == null) {
            return List.of();
        }
        
        return response.data();
    }
    
    /**
     * Get the current publication (using the configured publication ID)
     */
    @Tool(name = "beehiiv_get_current_publication", description = "Get information about the currently configured publication")
    public PublicationResponse getCurrentPublication() {
        if (!beehiivProperties.hasDefaultPublicationId()) {
            throw new IllegalArgumentException("No default publication ID configured. Set BEEHIIV_PUBLICATION_ID environment variable.");
        }
        return getPublicationById(this.defaultPublicationId);
    }
    
    /**
     * Search publications by name (case-insensitive partial match)
     */
    @Tool(name = "beehiiv_search_publications", description = "Search publications by name using case-insensitive partial matching.")
    public List<Publication> searchPublicationsByName(
            @ToolParam(description = "Search term to match against publication names (case-insensitive)") String searchTerm,
            @ToolParam(description = "Number of publications to return (1-100, default 10)", required = false) Integer limit,
            @ToolParam(description = "Field to order by: created, name", required = false) String orderBy,
            @ToolParam(description = "Sort direction: asc or desc", required = false) String direction) {
        if (searchTerm == null || searchTerm.isBlank()) {
            throw new IllegalArgumentException(ApiConstants.ERROR_NULL_BLANK_SEARCH_TERM);
        }
        
        PublicationsResponse response = getAllPublications(limit, 1, orderBy, direction);
        
        if (response == null || response.data() == null) {
            return List.of();
        }
        
        return response.data().stream()
                .filter(pub -> pub.name() != null && 
                        pub.name().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * List all publications accessible with the current API key
     */
    @Tool(name = "beehiiv_list_accessible_publications", description = "List all publications accessible with the current API key")
    public PublicationsResponse listAccessiblePublications(
            @ToolParam(description = "Number of publications to return (1-100, default 50)", required = false) Integer limit) {
        // Use a larger default limit to show more publications
        Integer effectiveLimit = limit != null ? limit : 50;
        return getAllPublications(effectiveLimit, 1, "created", "desc");
    }
    
}