package dev.danvega.beehiiv;

import dev.danvega.beehiiv.post.Post;
import dev.danvega.beehiiv.post.PostResponse;
import dev.danvega.beehiiv.post.PostsResponse;
import dev.danvega.beehiiv.publication.Publication;
import dev.danvega.beehiiv.publication.PublicationResponse;
import dev.danvega.beehiiv.publication.PublicationsResponse;
import dev.danvega.beehiiv.subscription.CustomField;
import dev.danvega.beehiiv.subscription.CustomFieldResponse;
import dev.danvega.beehiiv.subscription.CustomFieldsResponse;
import dev.danvega.beehiiv.subscription.Subscription;
import dev.danvega.beehiiv.subscription.SubscriptionResponse;
import dev.danvega.beehiiv.subscription.SubscriptionsResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Utility class for creating mock API responses for testing
 */
public class MockApiResponses {

    // ==================== POST RESPONSES ====================
    
    public static PostResponse createSamplePostResponse() {
        return new PostResponse(createSamplePost());
    }
    
    public static PostsResponse createSamplePostsResponse() {
        return new PostsResponse(
            List.of(createSamplePost(), createSecondSamplePost()),
            null // No pagination info for basic tests
        );
    }
    
    public static Post createSamplePost() {
        return new Post(
            "post_123456789",
            "Sample Newsletter Post",
            "This is a sample newsletter post for testing",
            "sample-newsletter-post",
            "2024-01-15",
            LocalDateTime.of(2024, 1, 15, 10, 0),
            1705320000L, // publish_date timestamp
            1705320000L, // displayed_date timestamp  
            LocalDateTime.of(2024, 1, 15, 12, 0),
            LocalDateTime.of(2024, 1, 15, 11, 0),
            "confirmed",
            false, // free_unlock
            false, // split_tested
            List.of("John Doe", "Jane Smith"),
            "Welcome to Our Newsletter!",
            "Check out our latest insights...",
            "https://example.com/thumbnail.jpg",
            "https://example.com/post/sample-newsletter-post",
            "all",
            "both",
            List.of("free", "premium"),
            List.of("newsletter", "updates"),
            false, // hidden_from_feed
            "Sample newsletter post with great insights",
            "Sample Newsletter Post",
            Map.of("email", Map.of("html", "<p>Email content</p>")),
            Map.of("opens", 150, "clicks", 25),
            List.of(),
            "<p>This is the HTML content</p>",
            "# This is the Markdown content",
            "Brief summary of the post content",
            // New fields from enhanced model
            "<p>Free web content</p>",
            "<p>Premium web content</p>",
            "<p>Premium email content</p>",
            "<rss>Free RSS content</rss>",
            LocalDateTime.of(2024, 1, 15, 9, 0),
            LocalDateTime.of(2024, 1, 15, 12, 30),
            LocalDateTime.of(2024, 1, 15, 12, 0),
            500, // emailDeliveredCount
            150, // emailOpenCount
            25,  // emailClickCount
            75,  // webViewCount
            30,  // totalClickCount
            "template_123",
            "pub_test123"
        );
    }
    
    public static Post createSecondSamplePost() {
        return new Post(
            "post_987654321",
            "Another Sample Post",
            "Second post for testing pagination",
            "another-sample-post",
            "2024-01-10",
            LocalDateTime.of(2024, 1, 10, 14, 0),
            1704888000L,
            1704888000L,
            LocalDateTime.of(2024, 1, 10, 16, 0),
            LocalDateTime.of(2024, 1, 10, 15, 0),
            "draft",
            true, // free_unlock
            false,
            List.of("Jane Smith"),
            "Another Great Post",
            "More insights coming your way...",
            "https://example.com/thumbnail2.jpg",
            "https://example.com/post/another-sample-post",
            "free",
            "email",
            List.of("free"),
            List.of("updates", "tips"),
            false,
            "Another sample post for testing",
            "Another Sample Post",
            Map.of("email", Map.of("html", "<p>Another email content</p>")),
            Map.of("opens", 85, "clicks", 12),
            List.of(),
            "<p>Another HTML content</p>",
            "# Another Markdown content",
            "Summary of another post",
            // New fields
            "<p>Another free web content</p>",
            null, // No premium web content
            null, // No premium email content
            "<rss>Another RSS content</rss>",
            LocalDateTime.of(2024, 1, 10, 13, 0),
            null, // Not sent yet
            null, // Not published yet
            0,   // emailDeliveredCount
            0,   // emailOpenCount
            0,   // emailClickCount
            0,   // webViewCount
            0,   // totalClickCount
            "template_456",
            "pub_test123"
        );
    }
    
    /**
     * Create an empty posts response for testing edge cases
     */
    public static PostsResponse createEmptyPostsResponse() {
        return new PostsResponse(List.of(), null);
    }
    
    // ==================== PUBLICATION RESPONSES ====================
    
    public static PublicationResponse createSamplePublicationResponse() {
        return new PublicationResponse(createSamplePublication());
    }
    
    public static PublicationsResponse createSamplePublicationsResponse() {
        return new PublicationsResponse(
            List.of(createSamplePublication(), createSecondSamplePublication()),
            10,  // limit
            1,   // page
            2,   // totalResults
            1    // totalPages
        );
    }
    
    public static Publication createSamplePublication() {
        return new Publication(
            "pub_test123",
            "Test Newsletter",
            "Test Organization",
            true, // referralProgramEnabled
            1673874000L, // created timestamp
            Map.of(
                "subscribers", 1250,
                "posts", 45,
                "open_rate", 35.5,
                "click_rate", 8.2
            ),
            "A test newsletter for development and testing purposes",
            "https://testnewsletter.example.com",
            "newsletter.test.com",
            "Test Newsletter Team",
            "hello@testnewsletter.example.com",
            LocalDateTime.of(2023, 1, 16, 10, 0),
            LocalDateTime.of(2024, 1, 15, 12, 0),
            1250, // subscriberCount
            45,   // postCount
            true, // premiumEnabled
            true  // boostEnabled
        );
    }
    
    public static Publication createSecondSamplePublication() {
        return new Publication(
            "pub_456789",
            "Another Test Publication",
            "Another Organization",
            false, // referralProgramEnabled
            1673960400L, // created timestamp
            Map.of(
                "subscribers", 500,
                "posts", 12,
                "open_rate", 42.1,
                "click_rate", 5.8
            ),
            "Another test publication for testing",
            "https://another.example.com",
            null, // no custom domain
            "Another Team",
            "team@another.example.com",
            LocalDateTime.of(2023, 1, 17, 14, 0),
            LocalDateTime.of(2024, 1, 10, 16, 0),
            500,  // subscriberCount
            12,   // postCount
            false, // premiumEnabled
            false  // boostEnabled
        );
    }
    
    // ==================== SUBSCRIPTION RESPONSES ====================
    
    public static SubscriptionResponse createSampleSubscriptionResponse() {
        return new SubscriptionResponse(createSampleSubscription());
    }
    
    public static SubscriptionsResponse createSampleSubscriptionsResponse() {
        return new SubscriptionsResponse(
            List.of(createSampleSubscription(), createSecondSampleSubscription()),
            2,   // totalResults
            1,   // page
            10   // limit
        );
    }
    
    public static Subscription createSampleSubscription() {
        return new Subscription(
            "sub_123456789",
            "test@example.com",
            "active",
            1673874000L, // created timestamp
            LocalDateTime.of(2023, 1, 16, 10, 0),
            LocalDateTime.of(2024, 1, 15, 12, 0),
            "free",
            "newsletter",
            "email",
            "welcome_series",
            "https://example.com",
            "https://example.com/newsletter",
            List.of(
                new Subscription.CustomFieldValue("name", "John Doe", "text"),
                new Subscription.CustomFieldValue("age", 30, "number"),
                new Subscription.CustomFieldValue("newsletter_frequency", "weekly", "list")
            ),
            List.of(), // No premium tiers for free subscriber
            new Subscription.SubscriptionStats(
                25, // opens
                5,  // clicks
                0.35, // open rate
                0.08, // click rate
                LocalDateTime.of(2024, 1, 14, 9, 30),
                LocalDateTime.of(2024, 1, 13, 15, 45)
            ),
            List.of(), // No referrals
            false, // doubleOptOverride
            false, // reactivated
            true,  // sendWelcomeEmail
            null,  // no stripe customer ID for free tier
            null,  // not unsubscribed
            "web"  // subscription method
        );
    }
    
    public static Subscription createSecondSampleSubscription() {
        return new Subscription(
            "sub_987654321",
            "premium@example.com",
            "active",
            1673960400L, // created timestamp
            LocalDateTime.of(2023, 1, 17, 14, 0),
            LocalDateTime.of(2024, 1, 10, 16, 0),
            "premium",
            "social_media",
            "social",
            "growth_campaign",
            "https://twitter.com",
            "https://t.co/newsletter",
            List.of(
                new Subscription.CustomFieldValue("name", "Jane Smith", "text"),
                new Subscription.CustomFieldValue("company", "Tech Corp", "text"),
                new Subscription.CustomFieldValue("premium_since", "2023-06-15", "date")
            ),
            List.of(
                new Subscription.SubscriptionPremiumTier(
                    "tier_premium",
                    "Premium Access",
                    999, // $9.99
                    "Access to premium content and features"
                )
            ),
            new Subscription.SubscriptionStats(
                45, // opens
                12, // clicks
                0.52, // open rate
                0.15, // click rate
                LocalDateTime.of(2024, 1, 15, 8, 15),
                LocalDateTime.of(2024, 1, 14, 11, 20)
            ),
            List.of(
                new Subscription.ReferralSubscription("sub_ref1", "friend@example.com", "active")
            ),
            true,  // doubleOptOverride
            false, // reactivated
            true,  // sendWelcomeEmail
            "cus_stripe123", // stripe customer ID
            null,  // not unsubscribed
            "api"  // subscription method
        );
    }
    
    // ==================== CUSTOM FIELD RESPONSES ====================
    
    public static CustomFieldResponse createSampleCustomFieldResponse() {
        return new CustomFieldResponse(createSampleCustomField());
    }
    
    public static CustomFieldsResponse createSampleCustomFieldsResponse() {
        return new CustomFieldsResponse(
            List.of(createSampleCustomField(), createSecondSampleCustomField(), createThirdSampleCustomField()),
            3,   // totalResults
            1,   // page
            10   // limit
        );
    }
    
    public static CustomField createSampleCustomField() {
        return new CustomField(
            "cf_123456789",
            "name",
            "text",
            true, // required
            LocalDateTime.of(2023, 1, 16, 10, 0),
            LocalDateTime.of(2024, 1, 15, 12, 0),
            null, // no default value
            null, // no options for text field
            "Subscriber's full name"
        );
    }
    
    public static CustomField createSecondSampleCustomField() {
        return new CustomField(
            "cf_987654321",
            "newsletter_frequency",
            "list",
            false, // not required
            LocalDateTime.of(2023, 1, 17, 14, 0),
            LocalDateTime.of(2024, 1, 10, 16, 0),
            "weekly", // default value
            List.of("daily", "weekly", "monthly"), // options
            "How often the subscriber wants to receive newsletters"
        );
    }
    
    public static CustomField createThirdSampleCustomField() {
        return new CustomField(
            "cf_555444333",
            "age",
            "number",
            false, // not required
            LocalDateTime.of(2023, 2, 1, 9, 0),
            LocalDateTime.of(2024, 1, 5, 10, 30),
            null, // no default value
            null, // no options for number field
            "Subscriber's age"
        );
    }
    
    // ==================== EMPTY RESPONSES ====================
    
    public static PublicationsResponse createEmptyPublicationsResponse() {
        return new PublicationsResponse(List.of(), 10, 1, 0, 0);
    }
    
    public static SubscriptionsResponse createEmptySubscriptionsResponse() {
        return new SubscriptionsResponse(List.of(), 0, 1, 10);
    }
    
    public static CustomFieldsResponse createEmptyCustomFieldsResponse() {
        return new CustomFieldsResponse(List.of(), 0, 1, 10);
    }
    
    // ==================== ERROR RESPONSES ====================
    
    /**
     * Create sample error response content
     */
    public static String createErrorResponseBody(int statusCode, String message) {
        return String.format("""
            {
                "error": {
                    "status": %d,
                    "message": "%s",
                    "code": "API_ERROR"
                }
            }
            """, statusCode, message);
    }
    
    /**
     * Create sample validation error response
     */
    public static String createValidationErrorResponseBody(String field, String issue) {
        return String.format("""
            {
                "error": {
                    "status": 400,
                    "message": "Validation failed",
                    "code": "VALIDATION_ERROR",
                    "details": {
                        "field": "%s",
                        "issue": "%s"
                    }
                }
            }
            """, field, issue);
    }
    
    /**
     * Create sample JSON for testing custom fields
     */
    public static String createSampleCustomFieldsJson() {
        return """
            {
                "name": "John Doe",
                "age": 30,
                "newsletter_frequency": "weekly",
                "marketing_emails": true
            }
            """;
    }
    
    /**
     * Create invalid JSON for testing error handling
     */
    public static String createInvalidJson() {
        return "{ invalid json structure";
    }
}