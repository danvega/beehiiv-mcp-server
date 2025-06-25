package dev.danvega.beehiiv.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record Subscription(
    String id,
    
    String email,
    
    String status,
    
    @JsonProperty("created")
    Long created,
    
    @JsonProperty("created_at")
    LocalDateTime createdAt,
    
    @JsonProperty("updated_at")
    LocalDateTime updatedAt,
    
    @JsonProperty("subscription_tier")
    String subscriptionTier,
    
    @JsonProperty("utm_source")
    String utmSource,
    
    @JsonProperty("utm_medium")
    String utmMedium,
    
    @JsonProperty("utm_campaign")
    String utmCampaign,
    
    @JsonProperty("referring_site")
    String referringSite,
    
    @JsonProperty("referrer_url")
    String referrerUrl,
    
    @JsonProperty("custom_fields")
    List<CustomFieldValue> customFields,
    
    @JsonProperty("subscription_premium_tiers")
    List<SubscriptionPremiumTier> subscriptionPremiumTiers,
    
    @JsonProperty("stats")
    SubscriptionStats stats,
    
    @JsonProperty("referrals")
    List<ReferralSubscription> referrals,
    
    @JsonProperty("double_opt_override")
    Boolean doubleOptOverride,
    
    @JsonProperty("reactivated")
    Boolean reactivated,
    
    @JsonProperty("send_welcome_email")
    Boolean sendWelcomeEmail,
    
    @JsonProperty("stripe_customer_id")
    String stripeCustomerId,
    
    @JsonProperty("unsubscribed_at")
    LocalDateTime unsubscribedAt,
    
    @JsonProperty("subscription_method")
    String subscriptionMethod
) {
    public record CustomFieldValue(
        String name,
        Object value,
        String type
    ) {}
    
    public record SubscriptionPremiumTier(
        String id,
        String name,
        @JsonProperty("price_cents")
        Integer priceCents,
        String description
    ) {}
    
    public record SubscriptionStats(
        @JsonProperty("opens")
        Integer opens,
        
        @JsonProperty("clicks")
        Integer clicks,
        
        @JsonProperty("open_rate")
        Double openRate,
        
        @JsonProperty("click_rate")
        Double clickRate,
        
        @JsonProperty("last_open_at")
        LocalDateTime lastOpenAt,
        
        @JsonProperty("last_click_at")
        LocalDateTime lastClickAt
    ) {}
    
    public record ReferralSubscription(
        String id,
        String email,
        String status
    ) {}
}