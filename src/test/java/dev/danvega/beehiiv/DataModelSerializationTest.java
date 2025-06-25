package dev.danvega.beehiiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DataModelSerializationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        // Configure to ignore unknown properties during deserialization
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Nested
    @DisplayName("Post Model Serialization Tests")
    class PostModelSerializationTests {

        @Test
        @DisplayName("Should serialize and deserialize Post object")
        void shouldSerializeAndDeserializePostObject() throws Exception {
            Post originalPost = MockApiResponses.createSamplePost();
            
            // Serialize to JSON
            String json = objectMapper.writeValueAsString(originalPost);
            assertNotNull(json);
            assertFalse(json.isEmpty());
            
            // Verify JSON contains expected fields
            assertTrue(json.contains("\"id\":\"post_123456789\""));
            assertTrue(json.contains("\"title\":\"Sample Newsletter Post\""));
            assertTrue(json.contains("\"status\":\"confirmed\""));
            
            // Deserialize back to object
            Post deserializedPost = objectMapper.readValue(json, Post.class);
            
            // Verify all fields are preserved
            assertEquals(originalPost.id(), deserializedPost.id());
            assertEquals(originalPost.title(), deserializedPost.title());
            assertEquals(originalPost.subtitle(), deserializedPost.subtitle());
            assertEquals(originalPost.slug(), deserializedPost.slug());
            assertEquals(originalPost.status(), deserializedPost.status());
            assertEquals(originalPost.authors(), deserializedPost.authors());
            assertEquals(originalPost.contentTags(), deserializedPost.contentTags());
            assertEquals(originalPost.audience(), deserializedPost.audience());
            assertEquals(originalPost.publicationId(), deserializedPost.publicationId());
        }

        @Test
        @DisplayName("Should serialize and deserialize PostResponse object")
        void shouldSerializeAndDeserializePostResponseObject() throws Exception {
            PostResponse originalResponse = MockApiResponses.createSamplePostResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            assertNotNull(json);
            assertTrue(json.contains("\"data\""));
            
            PostResponse deserializedResponse = objectMapper.readValue(json, PostResponse.class);
            assertEquals(originalResponse.data().id(), deserializedResponse.data().id());
            assertEquals(originalResponse.data().title(), deserializedResponse.data().title());
        }

        @Test
        @DisplayName("Should serialize and deserialize PostsResponse object")
        void shouldSerializeAndDeserializePostsResponseObject() throws Exception {
            PostsResponse originalResponse = MockApiResponses.createSamplePostsResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            assertNotNull(json);
            assertTrue(json.contains("\"data\""));
            
            PostsResponse deserializedResponse = objectMapper.readValue(json, PostsResponse.class);
            assertEquals(originalResponse.data().size(), deserializedResponse.data().size());
            assertEquals(originalResponse.data().get(0).id(), deserializedResponse.data().get(0).id());
        }

        @Test
        @DisplayName("Should handle null and empty values in Post")
        void shouldHandleNullAndEmptyValuesInPost() throws Exception {
            Post postWithNulls = new Post(
                "post_test", "Test Title", null, "test-slug", "2024-01-15",
                LocalDateTime.now(), 1705320000L, 1705320000L, 
                LocalDateTime.now(), LocalDateTime.now(), "confirmed",
                false, false, List.of(), "Email Subject", null,
                null, "https://example.com/post", "all", "both",
                List.of(), null, false, null, null,
                Map.of(), Map.of(), List.of(), "<p>Content</p>",
                "# Content", null, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                0, 0, 0, 0, 0, null, "pub_test"
            );
            
            String json = objectMapper.writeValueAsString(postWithNulls);
            Post deserializedPost = objectMapper.readValue(json, Post.class);
            
            assertEquals(postWithNulls.id(), deserializedPost.id());
            assertEquals(postWithNulls.title(), deserializedPost.title());
            assertNull(deserializedPost.subtitle());
        }
    }

    @Nested
    @DisplayName("Publication Model Serialization Tests")
    class PublicationModelSerializationTests {

        @Test
        @DisplayName("Should serialize and deserialize Publication object")
        void shouldSerializeAndDeserializePublicationObject() throws Exception {
            Publication originalPublication = MockApiResponses.createSamplePublication();
            
            String json = objectMapper.writeValueAsString(originalPublication);
            assertNotNull(json);
            assertTrue(json.contains("\"id\":\"pub_test123\""));
            assertTrue(json.contains("\"name\":\"Test Newsletter\""));
            
            Publication deserializedPublication = objectMapper.readValue(json, Publication.class);
            
            assertEquals(originalPublication.id(), deserializedPublication.id());
            assertEquals(originalPublication.name(), deserializedPublication.name());
            assertEquals(originalPublication.organizationName(), deserializedPublication.organizationName());
            assertEquals(originalPublication.referralProgramEnabled(), deserializedPublication.referralProgramEnabled());
            assertEquals(originalPublication.stats(), deserializedPublication.stats());
            assertEquals(originalPublication.description(), deserializedPublication.description());
        }

        @Test
        @DisplayName("Should serialize and deserialize PublicationResponse object")
        void shouldSerializeAndDeserializePublicationResponseObject() throws Exception {
            PublicationResponse originalResponse = MockApiResponses.createSamplePublicationResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            PublicationResponse deserializedResponse = objectMapper.readValue(json, PublicationResponse.class);
            
            assertEquals(originalResponse.data().id(), deserializedResponse.data().id());
            assertEquals(originalResponse.data().name(), deserializedResponse.data().name());
        }

        @Test
        @DisplayName("Should serialize and deserialize PublicationsResponse object")
        void shouldSerializeAndDeserializePublicationsResponseObject() throws Exception {
            PublicationsResponse originalResponse = MockApiResponses.createSamplePublicationsResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            PublicationsResponse deserializedResponse = objectMapper.readValue(json, PublicationsResponse.class);
            
            assertEquals(originalResponse.data().size(), deserializedResponse.data().size());
            assertEquals(originalResponse.limit(), deserializedResponse.limit());
            assertEquals(originalResponse.page(), deserializedResponse.page());
            assertEquals(originalResponse.totalResults(), deserializedResponse.totalResults());
            assertEquals(originalResponse.totalPages(), deserializedResponse.totalPages());
        }

        @Test
        @DisplayName("Should handle publication stats as Map")
        void shouldHandlePublicationStatsAsMap() throws Exception {
            Map<String, Object> stats = Map.of(
                "subscribers", 1000,
                "posts", 50,
                "open_rate", 35.5,
                "click_rate", 8.2
            );
            
            Publication publication = new Publication(
                "pub_test", "Test Pub", "Test Org", true, 1673874000L, stats,
                "Description", "https://test.com", null, "Team", "test@test.com",
                LocalDateTime.now(), LocalDateTime.now(), 1000, 50, true, true
            );
            
            String json = objectMapper.writeValueAsString(publication);
            Publication deserializedPublication = objectMapper.readValue(json, Publication.class);
            
            assertEquals(stats, deserializedPublication.stats());
        }
    }

    @Nested
    @DisplayName("Subscription Model Serialization Tests")
    class SubscriptionModelSerializationTests {

        @Test
        @DisplayName("Should serialize and deserialize Subscription object")
        void shouldSerializeAndDeserializeSubscriptionObject() throws Exception {
            Subscription originalSubscription = MockApiResponses.createSampleSubscription();
            
            String json = objectMapper.writeValueAsString(originalSubscription);
            assertNotNull(json);
            assertTrue(json.contains("\"id\":\"sub_123456789\""));
            assertTrue(json.contains("\"email\":\"test@example.com\""));
            
            Subscription deserializedSubscription = objectMapper.readValue(json, Subscription.class);
            
            assertEquals(originalSubscription.id(), deserializedSubscription.id());
            assertEquals(originalSubscription.email(), deserializedSubscription.email());
            assertEquals(originalSubscription.status(), deserializedSubscription.status());
            assertEquals(originalSubscription.subscriptionTier(), deserializedSubscription.subscriptionTier());
            assertEquals(originalSubscription.utmSource(), deserializedSubscription.utmSource());
            assertEquals(originalSubscription.customFields(), deserializedSubscription.customFields());
        }

        @Test
        @DisplayName("Should serialize and deserialize SubscriptionResponse object")
        void shouldSerializeAndDeserializeSubscriptionResponseObject() throws Exception {
            SubscriptionResponse originalResponse = MockApiResponses.createSampleSubscriptionResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            SubscriptionResponse deserializedResponse = objectMapper.readValue(json, SubscriptionResponse.class);
            
            assertEquals(originalResponse.data().id(), deserializedResponse.data().id());
            assertEquals(originalResponse.data().email(), deserializedResponse.data().email());
        }

        @Test
        @DisplayName("Should serialize and deserialize SubscriptionsResponse object")
        void shouldSerializeAndDeserializeSubscriptionsResponseObject() throws Exception {
            SubscriptionsResponse originalResponse = MockApiResponses.createSampleSubscriptionsResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            SubscriptionsResponse deserializedResponse = objectMapper.readValue(json, SubscriptionsResponse.class);
            
            assertEquals(originalResponse.data().size(), deserializedResponse.data().size());
            assertEquals(originalResponse.totalResults(), deserializedResponse.totalResults());
            assertEquals(originalResponse.page(), deserializedResponse.page());
            assertEquals(originalResponse.limit(), deserializedResponse.limit());
        }

        @Test
        @DisplayName("Should handle custom fields in subscription")
        void shouldHandleCustomFieldsInSubscription() throws Exception {
            List<Subscription.CustomFieldValue> customFields = List.of(
                new Subscription.CustomFieldValue("name", "John Doe", "text"),
                new Subscription.CustomFieldValue("age", 30, "number"),
                new Subscription.CustomFieldValue("active", true, "boolean")
            );
            
            Subscription subscription = new Subscription(
                "sub_test", "test@example.com", "active", 1673874000L,
                LocalDateTime.now(), LocalDateTime.now(), "free", "newsletter",
                "email", "welcome", "https://example.com", "https://ref.com",
                customFields, List.of(), null, List.of(), false, false, true,
                null, null, "web"
            );
            
            String json = objectMapper.writeValueAsString(subscription);
            Subscription deserializedSubscription = objectMapper.readValue(json, Subscription.class);
            
            assertEquals(customFields.size(), deserializedSubscription.customFields().size());
            assertEquals("John Doe", deserializedSubscription.customFields().get(0).value());
            assertEquals(30, deserializedSubscription.customFields().get(1).value());
            assertEquals(true, deserializedSubscription.customFields().get(2).value());
        }
    }

    @Nested
    @DisplayName("Custom Field Model Serialization Tests")
    class CustomFieldModelSerializationTests {

        @Test
        @DisplayName("Should serialize and deserialize CustomField object")
        void shouldSerializeAndDeserializeCustomFieldObject() throws Exception {
            CustomField originalCustomField = MockApiResponses.createSampleCustomField();
            
            String json = objectMapper.writeValueAsString(originalCustomField);
            assertNotNull(json);
            assertTrue(json.contains("\"id\":\"cf_123456789\""));
            assertTrue(json.contains("\"name\":\"name\""));
            assertTrue(json.contains("\"type\":\"text\""));
            
            CustomField deserializedCustomField = objectMapper.readValue(json, CustomField.class);
            
            assertEquals(originalCustomField.id(), deserializedCustomField.id());
            assertEquals(originalCustomField.name(), deserializedCustomField.name());
            assertEquals(originalCustomField.type(), deserializedCustomField.type());
            assertEquals(originalCustomField.isRequired(), deserializedCustomField.isRequired());
            assertEquals(originalCustomField.options(), deserializedCustomField.options());
            assertEquals(originalCustomField.description(), deserializedCustomField.description());
        }

        @Test
        @DisplayName("Should serialize and deserialize CustomFieldResponse object")
        void shouldSerializeAndDeserializeCustomFieldResponseObject() throws Exception {
            CustomFieldResponse originalResponse = MockApiResponses.createSampleCustomFieldResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            CustomFieldResponse deserializedResponse = objectMapper.readValue(json, CustomFieldResponse.class);
            
            assertEquals(originalResponse.data().id(), deserializedResponse.data().id());
            assertEquals(originalResponse.data().name(), deserializedResponse.data().name());
        }

        @Test
        @DisplayName("Should serialize and deserialize CustomFieldsResponse object")
        void shouldSerializeAndDeserializeCustomFieldsResponseObject() throws Exception {
            CustomFieldsResponse originalResponse = MockApiResponses.createSampleCustomFieldsResponse();
            
            String json = objectMapper.writeValueAsString(originalResponse);
            CustomFieldsResponse deserializedResponse = objectMapper.readValue(json, CustomFieldsResponse.class);
            
            assertEquals(originalResponse.data().size(), deserializedResponse.data().size());
            assertEquals(originalResponse.totalResults(), deserializedResponse.totalResults());
            assertEquals(originalResponse.page(), deserializedResponse.page());
            assertEquals(originalResponse.limit(), deserializedResponse.limit());
        }

        @Test
        @DisplayName("Should handle list type custom field with options")
        void shouldHandleListTypeCustomFieldWithOptions() throws Exception {
            List<String> options = List.of("option1", "option2", "option3");
            CustomField listField = new CustomField(
                "cf_list", "choices", "list", false,
                LocalDateTime.now(), LocalDateTime.now(),
                "option1", options, "A list field with options"
            );
            
            String json = objectMapper.writeValueAsString(listField);
            CustomField deserializedField = objectMapper.readValue(json, CustomField.class);
            
            assertEquals(listField.options(), deserializedField.options());
            assertEquals(listField.defaultValue(), deserializedField.defaultValue());
            assertEquals("list", deserializedField.type());
        }
    }

    @Nested
    @DisplayName("Date Time Serialization Tests")
    class DateTimeSerializationTests {

        @Test
        @DisplayName("Should properly serialize and deserialize LocalDateTime fields")
        void shouldProperlySerializeAndDeserializeLocalDateTimeFields() throws Exception {
            LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
            
            CustomField fieldWithDateTime = new CustomField(
                "cf_datetime", "test_field", "text", false,
                testDateTime, testDateTime, null, null, "Test field"
            );
            
            String json = objectMapper.writeValueAsString(fieldWithDateTime);
            CustomField deserializedField = objectMapper.readValue(json, CustomField.class);
            
            assertEquals(testDateTime, deserializedField.createdAt());
            assertEquals(testDateTime, deserializedField.updatedAt());
        }

        @Test
        @DisplayName("Should handle null DateTime fields")
        void shouldHandleNullDateTimeFields() throws Exception {
            Post postWithNullDates = new Post(
                "post_test", "Test Post", "Subtitle", "test-post", "2024-01-15",
                null, 1705320000L, 1705320000L, null, null, "confirmed",
                false, false, List.of(), "Subject", "Preview", null,
                "https://example.com/post", "all", "both", List.of(), List.of(),
                false, "Description", "SEO Title", Map.of(), Map.of(), List.of(),
                "<p>Content</p>", "# Content", "Summary", null, null, null, null,
                null, null, null, 0, 0, 0, 0, 0, null, "pub_test"
            );
            
            String json = objectMapper.writeValueAsString(postWithNullDates);
            Post deserializedPost = objectMapper.readValue(json, Post.class);
            
            assertNull(deserializedPost.createdAt());
            assertNull(deserializedPost.updatedAt());
            assertEquals("post_test", deserializedPost.id());
        }
    }

    @Nested
    @DisplayName("Complex Data Structure Tests")
    class ComplexDataStructureTests {

        @Test
        @DisplayName("Should handle nested objects in subscription")
        void shouldHandleNestedObjectsInSubscription() throws Exception {
            Subscription.SubscriptionStats stats = new Subscription.SubscriptionStats(
                25, 5, 0.35, 0.08,
                LocalDateTime.of(2024, 1, 14, 9, 30),
                LocalDateTime.of(2024, 1, 13, 15, 45)
            );
            
            Subscription.SubscriptionPremiumTier tier = new Subscription.SubscriptionPremiumTier(
                "tier_premium", "Premium Access", 999, "Premium features"
            );
            
            Subscription subscriptionWithNested = new Subscription(
                "sub_complex", "test@example.com", "active", 1673874000L,
                LocalDateTime.now(), LocalDateTime.now(), "premium", "newsletter",
                "email", "welcome", "https://example.com", "https://ref.com",
                List.of(), List.of(tier), stats, List.of(), false, false, true,
                "cus_stripe123", null, "api"
            );
            
            String json = objectMapper.writeValueAsString(subscriptionWithNested);
            Subscription deserializedSubscription = objectMapper.readValue(json, Subscription.class);
            
            assertNotNull(deserializedSubscription.stats());
            assertEquals(25, deserializedSubscription.stats().opens());
            assertEquals(1, deserializedSubscription.subscriptionPremiumTiers().size());
            assertEquals("Premium Access", deserializedSubscription.subscriptionPremiumTiers().get(0).name());
        }

        @Test
        @DisplayName("Should handle maps and lists in post content")
        void shouldHandleMapsAndListsInPostContent() throws Exception {
            Map<String, Map<String, String>> contentMap = Map.of(
                "email", Map.of("html", "<p>Email content</p>", "text", "Email content"),
                "web", Map.of("html", "<p>Web content</p>")
            );
            
            Map<String, Object> statsMap = Map.of(
                "opens", 150,
                "clicks", 25,
                "unique_opens", 120,
                "open_rate", 35.5
            );
            
            List<String> authors = List.of("John Doe", "Jane Smith");
            List<String> tags = List.of("newsletter", "updates", "news");
            
            Post postWithComplexData = new Post(
                "post_complex", "Complex Post", "Complex subtitle", "complex-post", "2024-01-15",
                LocalDateTime.now(), 1705320000L, 1705320000L,
                LocalDateTime.now(), LocalDateTime.now(), "confirmed",
                false, false, authors, "Email Subject", "Preview text", "https://thumb.jpg",
                "https://example.com/post", "all", "both", List.of("free", "premium"), tags,
                false, "Description", "SEO Title", contentMap, statsMap, List.of(),
                "<p>HTML content</p>", "# Markdown content", "Summary",
                "<p>Free web</p>", "<p>Premium web</p>", "<p>Premium email</p>", "<rss>RSS</rss>",
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                500, 150, 25, 75, 30, "template_123", "pub_test123"
            );
            
            String json = objectMapper.writeValueAsString(postWithComplexData);
            Post deserializedPost = objectMapper.readValue(json, Post.class);
            
            assertEquals(authors, deserializedPost.authors());
            assertEquals(tags, deserializedPost.contentTags());
            assertEquals(contentMap, deserializedPost.content());
            assertEquals(statsMap, deserializedPost.stats());
        }
    }

    @Nested
    @DisplayName("JSON Property Mapping Tests")
    class JsonPropertyMappingTests {

        @Test
        @DisplayName("Should correctly map snake_case JSON to camelCase fields")
        void shouldCorrectlyMapSnakeCaseJsonToCamelCaseFields() throws Exception {
            String jsonWithSnakeCase = """
                {
                    "id": "cf_test",
                    "name": "test_field",
                    "type": "text",
                    "is_required": true,
                    "created_at": "2024-01-15T10:30:00",
                    "updated_at": "2024-01-15T12:00:00",
                    "default_value": "default",
                    "description": "Test field"
                }
                """;
            
            CustomField customField = objectMapper.readValue(jsonWithSnakeCase, CustomField.class);
            
            assertEquals("cf_test", customField.id());
            assertEquals("test_field", customField.name());
            assertEquals("text", customField.type());
            assertEquals(true, customField.isRequired());
            assertEquals("default", customField.defaultValue());
            assertEquals("Test field", customField.description());
        }

        @Test
        @DisplayName("Should handle missing optional fields gracefully")
        void shouldHandleMissingOptionalFieldsGracefully() throws Exception {
            String minimalJson = """
                {
                    "id": "cf_minimal",
                    "name": "minimal_field",
                    "type": "text"
                }
                """;
            
            CustomField customField = objectMapper.readValue(minimalJson, CustomField.class);
            
            assertEquals("cf_minimal", customField.id());
            assertEquals("minimal_field", customField.name());
            assertEquals("text", customField.type());
            assertNull(customField.isRequired());
            assertNull(customField.defaultValue());
            assertNull(customField.description());
        }
    }
}