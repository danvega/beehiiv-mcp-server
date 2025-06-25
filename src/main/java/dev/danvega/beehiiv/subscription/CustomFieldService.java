package dev.danvega.beehiiv.subscription;

import dev.danvega.beehiiv.core.ApiException;
import dev.danvega.beehiiv.core.BeehiivProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public final class CustomFieldService {

    private static final Logger logger = LoggerFactory.getLogger(CustomFieldService.class);
    private final RestClient restClient;
    private final String defaultPublicationId;
    private final BeehiivProperties beehiivProperties;

    public CustomFieldService(RestClient restClient, BeehiivProperties beehiivProperties) {
        this.restClient = restClient;
        this.defaultPublicationId = beehiivProperties.defaultPublicationId();
        this.beehiivProperties = beehiivProperties;
        logger.info("Initializing CustomFieldService with default publication ID: {}", 
                defaultPublicationId != null ? defaultPublicationId : "none");
    }

    @Tool(name = "beehiiv_list_custom_fields", description = "List all custom fields for a publication.")
    public CustomFieldsResponse listCustomFields(
            @ToolParam(description = "Number of custom fields to return (1-100, default 50)", required = false) Integer limit,
            @ToolParam(description = "Page number for pagination (1+, default 1)", required = false) Integer page,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        
        String resolvedPublicationId = resolvePublicationId(publicationId);
        
        try {
            String uri = buildCustomFieldsUri(resolvedPublicationId, limit, page);
            logger.info("Listing custom fields for publication: {}", resolvedPublicationId);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(CustomFieldsResponse.class);
                    
        } catch (HttpClientErrorException e) {
            logger.error("Error listing custom fields for publication {}: Status: {}, Body: {}", 
                    resolvedPublicationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException("Error listing custom fields for publication " + resolvedPublicationId + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error listing custom fields for publication: {}", resolvedPublicationId, e);
            throw new ApiException("Unexpected error listing custom fields for publication " + resolvedPublicationId, e);
        }
    }

    @Tool(name = "beehiiv_get_custom_field", description = "Get details of a specific custom field.")
    public CustomFieldResponse getCustomField(
            @ToolParam(description = "Custom field ID to retrieve (required)") String customFieldId,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        
        if (customFieldId == null || customFieldId.isBlank()) {
            throw new IllegalArgumentException("Custom field ID is required");
        }
        
        String resolvedPublicationId = resolvePublicationId(publicationId);
        
        try {
            String uri = "/publications/" + resolvedPublicationId + "/custom_fields/" + customFieldId;
            logger.info("Retrieving custom field {} for publication: {}", customFieldId, resolvedPublicationId);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(CustomFieldResponse.class);
                    
        } catch (HttpClientErrorException e) {
            logger.error("Error retrieving custom field {} for publication {}: Status: {}, Body: {}", 
                    customFieldId, resolvedPublicationId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException("Error retrieving custom field " + customFieldId + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving custom field {} for publication: {}", customFieldId, resolvedPublicationId, e);
            throw new ApiException("Unexpected error retrieving custom field " + customFieldId, e);
        }
    }

    @Tool(name = "beehiiv_get_custom_field_by_name", description = "Find a custom field by name.")
    public CustomField getCustomFieldByName(
            @ToolParam(description = "Custom field name to search for (required)") String fieldName,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        
        if (fieldName == null || fieldName.isBlank()) {
            throw new IllegalArgumentException("Custom field name is required");
        }
        
        // Get all custom fields and search by name
        CustomFieldsResponse response = listCustomFields(100, 1, publicationId);
        
        if (response != null && response.data() != null) {
            return response.data().stream()
                    .filter(field -> fieldName.equals(field.name()))
                    .findFirst()
                    .orElse(null);
        }
        
        return null;
    }

    @Tool(name = "beehiiv_validate_custom_field_value", description = "Validate a value against a custom field's type and constraints.")
    public Map<String, Object> validateCustomFieldValue(
            @ToolParam(description = "Custom field name or ID (required)") String fieldIdentifier,
            @ToolParam(description = "Value to validate (required)") String value,
            @ToolParam(description = "Publication ID to query (optional, uses default if not provided)", required = false) String publicationId) {
        
        if (fieldIdentifier == null || fieldIdentifier.isBlank()) {
            throw new IllegalArgumentException("Custom field identifier is required");
        }
        
        // Try to get field by name first, then by ID
        CustomField field = getCustomFieldByName(fieldIdentifier, publicationId);
        if (field == null) {
            // Try by ID
            try {
                CustomFieldResponse response = getCustomField(fieldIdentifier, publicationId);
                field = response != null ? response.data() : null;
            } catch (Exception e) {
                // Field not found
            }
        }
        
        if (field == null) {
            return Map.of(
                "valid", false,
                "error", "Custom field not found: " + fieldIdentifier
            );
        }
        
        try {
            Object convertedValue = field.convertValue(value);
            boolean isValid = field.isValidValue(convertedValue);
            
            return Map.of(
                "valid", isValid,
                "convertedValue", convertedValue,
                "fieldType", field.type(),
                "fieldName", field.name()
            );
        } catch (Exception e) {
            return Map.of(
                "valid", false,
                "error", e.getMessage(),
                "fieldType", field.type(),
                "fieldName", field.name()
            );
        }
    }

    /**
     * Helper method to resolve the publication ID to use
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
     * Build URI for listing custom fields
     */
    private String buildCustomFieldsUri(String publicationId, Integer limit, Integer page) {
        StringBuilder uri = new StringBuilder("/publications/" + publicationId + "/custom_fields");
        
        if (limit != null || page != null) {
            uri.append("?");
            boolean hasParam = false;
            
            if (limit != null) {
                uri.append("limit=").append(limit);
                hasParam = true;
            }
            
            if (page != null) {
                if (hasParam) uri.append("&");
                uri.append("page=").append(page);
            }
        }
        
        return uri.toString();
    }
}