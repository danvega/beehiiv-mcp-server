package dev.danvega.beehiiv.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danvega.beehiiv.core.ApiConstants;
import dev.danvega.beehiiv.core.ApiException;
import dev.danvega.beehiiv.core.BeehiivProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public final class SubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);
    private final RestClient restClient;
    private final String defaultPublicationId;
    private final BeehiivProperties beehiivProperties;
    private final ObjectMapper objectMapper;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );

    public SubscriptionService(RestClient restClient, BeehiivProperties beehiivProperties, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.defaultPublicationId = beehiivProperties.defaultPublicationId();
        this.beehiivProperties = beehiivProperties;
        this.objectMapper = objectMapper;
        logger.info("Initializing SubscriptionService with default publication ID: {}", 
                defaultPublicationId != null ? defaultPublicationId : "none");
    }

    @Tool(name = "beehiiv_create_subscription", description = "Create a new subscription/subscriber for a publication.")
    public SubscriptionResponse createSubscription(
            @ToolParam(description = "Email address of the subscriber (required)") String email,
            @ToolParam(description = "Custom fields as JSON object (optional) - e.g., {\"name\": \"John\", \"age\": 30}", required = false) String customFields,
            @ToolParam(description = "Reactivate if subscriber exists (true/false, default: false)", required = false) Boolean reactivated,
            @ToolParam(description = "Send welcome email (true/false, default: true)", required = false) Boolean sendWelcomeEmail,
            @ToolParam(description = "Override double opt-in settings (true/false)", required = false) Boolean doubleOptOverride,
            @ToolParam(description = "UTM source for tracking", required = false) String utmSource,
            @ToolParam(description = "UTM medium for tracking", required = false) String utmMedium,
            @ToolParam(description = "UTM campaign for tracking", required = false) String utmCampaign,
            @ToolParam(description = "Referring site URL", required = false) String referringSite,
            @ToolParam(description = "Publication ID to subscribe to (optional, uses default if not provided)", required = false) String publicationId) {
        
        // Validate email
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email address is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        
        String resolvedPublicationId = resolvePublicationId(publicationId);
        
        try {
            Map<String, Object> requestBody = buildCreateSubscriptionRequest(
                email, customFields, reactivated, sendWelcomeEmail, doubleOptOverride,
                utmSource, utmMedium, utmCampaign, referringSite
            );
            
            String uri = "/publications/" + resolvedPublicationId + "/subscriptions";
            logger.info("Creating subscription for email: {} in publication: {}", email, resolvedPublicationId);
            
            return restClient.post()
                    .uri(uri)
                    .body(requestBody)
                    .retrieve()
                    .body(SubscriptionResponse.class);
                    
        } catch (HttpClientErrorException e) {
            logger.error("Error creating subscription for {}: Status: {}, Body: {}", 
                    email, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException("Error creating subscription for " + email + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error creating subscription for: {}", email, e);
            throw new ApiException("Unexpected error creating subscription for " + email, e);
        }
    }

    @Tool(name = "beehiiv_get_subscription_by_email", description = "Retrieve a subscription by email address.")
    public SubscriptionResponse getSubscriptionByEmail(
            @ToolParam(description = "Email address to search for (required)") String email,
            @ToolParam(description = "Comma-separated expand options: stats, custom_fields, referrals, subscription_premium_tiers", required = false) String expand,
            @ToolParam(description = "Publication ID to search in (optional, uses default if not provided)", required = false) String publicationId) {
        
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email address is required");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email address format");
        }
        
        String resolvedPublicationId = resolvePublicationId(publicationId);
        
        try {
            String uri = buildGetByEmailUri(resolvedPublicationId, email, expand);
            logger.info("Retrieving subscription for email: {} in publication: {}", email, resolvedPublicationId);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(SubscriptionResponse.class);
                    
        } catch (HttpClientErrorException e) {
            logger.error("Error retrieving subscription for {}: Status: {}, Body: {}", 
                    email, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException("Error retrieving subscription for " + email + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving subscription for: {}", email, e);
            throw new ApiException("Unexpected error retrieving subscription for " + email, e);
        }
    }

    @Tool(name = "beehiiv_get_subscription_by_id", description = "Retrieve a subscription by ID.")
    public SubscriptionResponse getSubscriptionById(
            @ToolParam(description = "Subscription ID to retrieve (required)") String subscriptionId,
            @ToolParam(description = "Comma-separated expand options: stats, custom_fields, referrals, subscription_premium_tiers", required = false) String expand,
            @ToolParam(description = "Publication ID to search in (optional, uses default if not provided)", required = false) String publicationId) {
        
        if (subscriptionId == null || subscriptionId.isBlank()) {
            throw new IllegalArgumentException("Subscription ID is required");
        }
        
        String resolvedPublicationId = resolvePublicationId(publicationId);
        
        try {
            String uri = buildGetByIdUri(resolvedPublicationId, subscriptionId, expand);
            logger.info("Retrieving subscription ID: {} in publication: {}", subscriptionId, resolvedPublicationId);
            
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(SubscriptionResponse.class);
                    
        } catch (HttpClientErrorException e) {
            logger.error("Error retrieving subscription {}: Status: {}, Body: {}", 
                    subscriptionId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApiException("Error retrieving subscription " + subscriptionId + ": " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error retrieving subscription: {}", subscriptionId, e);
            throw new ApiException("Unexpected error retrieving subscription " + subscriptionId, e);
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
     * Build the request body for creating a subscription
     */
    private Map<String, Object> buildCreateSubscriptionRequest(String email, String customFields, 
            Boolean reactivated, Boolean sendWelcomeEmail, Boolean doubleOptOverride,
            String utmSource, String utmMedium, String utmCampaign, String referringSite) {
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);
        
        if (reactivated != null) {
            requestBody.put("reactivated", reactivated);
        }
        
        if (sendWelcomeEmail != null) {
            requestBody.put("send_welcome_email", sendWelcomeEmail);
        }
        
        if (doubleOptOverride != null) {
            requestBody.put("double_opt_override", doubleOptOverride);
        }
        
        if (utmSource != null && !utmSource.isBlank()) {
            requestBody.put("utm_source", utmSource);
        }
        
        if (utmMedium != null && !utmMedium.isBlank()) {
            requestBody.put("utm_medium", utmMedium);
        }
        
        if (utmCampaign != null && !utmCampaign.isBlank()) {
            requestBody.put("utm_campaign", utmCampaign);
        }
        
        if (referringSite != null && !referringSite.isBlank()) {
            requestBody.put("referring_site", referringSite);
        }
        
        // Parse custom fields JSON if provided
        if (customFields != null && !customFields.isBlank()) {
            try {
                Map<String, Object> customFieldsMap = objectMapper.readValue(customFields, Map.class);
                requestBody.put("custom_fields", customFieldsMap);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Invalid custom fields JSON format: " + e.getMessage());
            }
        }
        
        return requestBody;
    }

    /**
     * Build URI for getting subscription by email
     */
    private String buildGetByEmailUri(String publicationId, String email, String expand) {
        StringBuilder uri = new StringBuilder("/publications/" + publicationId + "/subscriptions/by_email/" + email);
        
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
        }
        
        return uri.toString();
    }

    /**
     * Build URI for getting subscription by ID
     */
    private String buildGetByIdUri(String publicationId, String subscriptionId, String expand) {
        StringBuilder uri = new StringBuilder("/publications/" + publicationId + "/subscriptions/" + subscriptionId);
        
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
        }
        
        return uri.toString();
    }
    
}