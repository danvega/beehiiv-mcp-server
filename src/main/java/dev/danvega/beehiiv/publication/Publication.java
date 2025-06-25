package dev.danvega.beehiiv.publication;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public record Publication(
    String id,
    String name,
    
    @JsonProperty("organization_name")
    String organizationName,
    
    @JsonProperty("referral_program_enabled")
    boolean referralProgramEnabled,
    
    Long created,
    
    Map<String, Object> stats,
    
    String description,
    
    @JsonProperty("website_url")
    String websiteUrl,
    
    @JsonProperty("custom_domain")
    String customDomain,
    
    @JsonProperty("email_from_name")
    String emailFromName,
    
    @JsonProperty("email_from_address")
    String emailFromAddress,
    
    @JsonProperty("created_at")
    LocalDateTime createdAt,
    
    @JsonProperty("updated_at")
    LocalDateTime updatedAt,
    
    @JsonProperty("subscriber_count")
    Integer subscriberCount,
    
    @JsonProperty("post_count")
    Integer postCount,
    
    @JsonProperty("premium_enabled")
    boolean premiumEnabled,
    
    @JsonProperty("boost_enabled")
    boolean boostEnabled
) {
    // Validation constructor
    public Publication {
        // No validation needed for now, but this is where we would add it if needed
    }
}
