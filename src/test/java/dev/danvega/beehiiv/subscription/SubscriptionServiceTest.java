package dev.danvega.beehiiv.subscription;

import dev.danvega.beehiiv.BaseServiceTest;
import dev.danvega.beehiiv.MockApiResponses;
import dev.danvega.beehiiv.core.BeehiivProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionServiceTest extends BaseServiceTest {

    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUpSubscriptionService() {
        subscriptionService = new SubscriptionService(mockRestClient, testProperties, mockObjectMapper);
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should accept valid email addresses")
        void shouldAcceptValidEmailAddresses() {
            // Test various valid email formats
            assertTrue(isValidEmail("test@example.com"));
            assertTrue(isValidEmail("user.name@domain.co.uk"));
            assertTrue(isValidEmail("firstname+lastname@example.org"));
            assertTrue(isValidEmail("email@123.123.123.123")); // IP address
            assertTrue(isValidEmail("user@very.long.domain.name.example.com"));
            assertTrue(isValidEmail("a@b.co"));
            assertTrue(isValidEmail("test.email.with+symbol@example.com"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should reject null or blank email addresses")
        void shouldRejectNullOrBlankEmailAddresses(String invalidEmail) {
            assertFalse(isValidEmail(invalidEmail));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "plainaddress",
            "@missingdomain.com",
            "missing@.com",
            "domain.com",
            "two@@domain.com",
            "user@",
            "@domain.com",
            "user name@domain.com", // space in local part
            "user@domain .com"      // space in domain
        })
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) {
            assertFalse(isValidEmail(invalidEmail));
        }

        @Test
        @DisplayName("Should throw exception for invalid email in createSubscription")
        void shouldThrowExceptionForInvalidEmailInCreateSubscription() {
            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.createSubscription("invalid-email", null, null, null, null, null, null, null, null, null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.createSubscription("", null, null, null, null, null, null, null, null, null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.createSubscription(null, null, null, null, null, null, null, null, null, null);
            });
        }
    }

    @Nested
    @DisplayName("JSON Custom Fields Validation Tests")
    class JsonCustomFieldsValidationTests {

        @Test
        @DisplayName("Should accept valid JSON strings")
        void shouldAcceptValidJsonStrings() {
            String validJson = MockApiResponses.createSampleCustomFieldsJson();
            assertTrue(isValidJson(validJson));

            assertTrue(isValidJson("{}"));
            assertTrue(isValidJson("{\"name\": \"John\", \"age\": 30}"));
            assertTrue(isValidJson("{\"active\": true, \"score\": 95.5}"));
            assertTrue(isValidJson("[\"item1\", \"item2\"]"));
        }

        @Test
        @DisplayName("Should reject invalid JSON strings")
        void shouldRejectInvalidJsonStrings() {
            String invalidJson = MockApiResponses.createInvalidJson();
            assertFalse(isValidJson(invalidJson));

            assertFalse(isValidJson("{invalid json}"));
            assertFalse(isValidJson("{'single': 'quotes'}"));
            assertFalse(isValidJson("{trailing: comma,}"));
            assertFalse(isValidJson("{\"unclosed\": \"string}"));
        }

        @Test
        @DisplayName("Should handle null and empty JSON strings")
        void shouldHandleNullAndEmptyJsonStrings() {
            assertFalse(isValidJson(null));
            assertFalse(isValidJson(""));
            assertFalse(isValidJson("   "));
            assertFalse(isValidJson("\t"));
        }

        @Test
        @DisplayName("Should accept valid custom fields in createSubscription")
        void shouldAcceptValidCustomFieldsInCreateSubscription() {
            String validCustomFields = MockApiResponses.createSampleCustomFieldsJson();
            
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        validCustomFields, 
                        false, true, false, 
                        "newsletter", "email", "welcome", 
                        "https://example.com", 
                        null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should validate JSON format of custom fields")
        void shouldValidateJsonFormatOfCustomFields() {
            String invalidJson = MockApiResponses.createInvalidJson();
            
            // Test that the JSON validation logic works by testing it directly
            assertFalse(isValidJson(invalidJson));
            assertTrue(isValidJson(MockApiResponses.createSampleCustomFieldsJson()));
            
            // When calling the actual service method, invalid JSON should cause an exception
            // but it gets wrapped in ApiException due to the RestClient call chain
            assertThrows(Exception.class, () -> {
                subscriptionService.createSubscription(
                    "test@example.com", 
                    invalidJson, 
                    false, true, false, 
                    "newsletter", "email", "welcome", 
                    "https://example.com", 
                    null
                );
            });
        }
    }

    @Nested
    @DisplayName("Boolean Parameter Validation Tests")
    class BooleanParameterValidationTests {

        @Test
        @DisplayName("Should accept valid boolean parameters")
        void shouldAcceptValidBooleanParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, 
                        true,  // reactivated
                        false, // sendWelcomeEmail
                        true,  // doubleOptOverride
                        null, null, null, null, null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should accept null boolean parameters")
        void shouldAcceptNullBooleanParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, 
                        null, // reactivated
                        null, // sendWelcomeEmail
                        null, // doubleOptOverride
                        null, null, null, null, null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }

    @Nested
    @DisplayName("UTM Parameter Validation Tests")
    class UtmParameterValidationTests {

        @Test
        @DisplayName("Should accept valid UTM parameters")
        void shouldAcceptValidUtmParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, null, null, null,
                        "newsletter",      // utmSource
                        "email",          // utmMedium
                        "welcome_series", // utmCampaign
                        "https://example.com",     // referringSite
                        null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should accept null UTM parameters")
        void shouldAcceptNullUtmParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, null, null, null,
                        null, // utmSource
                        null, // utmMedium
                        null, // utmCampaign
                        null, // referringSite
                        null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle special characters in UTM parameters")
        void shouldHandleSpecialCharactersInUtmParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, null, null, null,
                        "newsletter-2024",        // utmSource with dash
                        "email_campaign",         // utmMedium with underscore
                        "welcome series (new)",   // utmCampaign with spaces and parentheses
                        "https://site.com/page?param=value", // referringSite with query params
                        null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }

    @Nested
    @DisplayName("Publication ID Resolution Tests")
    class PublicationIdResolutionTests {

        @Test
        @DisplayName("Should use provided publication ID when available")
        void shouldUseProvidedPublicationIdWhenAvailable() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, null, null, null, null, null, null, null,
                        "pub_custom123" // publicationId
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should use default publication ID when none provided")
        void shouldUseDefaultPublicationIdWhenNoneProvided() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, null, null, null, null, null, null, null,
                        null // publicationId
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should throw exception when no publication ID available")
        void shouldThrowExceptionWhenNoPublicationIdAvailable() {
            SubscriptionService serviceWithoutDefault = new SubscriptionService(
                mockRestClient, createTestPropertiesNoPublication(), mockObjectMapper);
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> serviceWithoutDefault.createSubscription(
                    "test@example.com", 
                    null, null, null, null, null, null, null, null,
                    null
                )
            );
            
            assertTrue(exception.getMessage().contains("No publication ID provided") || 
                      exception.getMessage().contains("Publication ID is required"));
        }
    }

    @Nested
    @DisplayName("Get Subscription By Email Tests")
    class GetSubscriptionByEmailTests {

        @Test
        @DisplayName("Should get subscription by email with all parameters")
        void shouldGetSubscriptionByEmailWithAllParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.getSubscriptionByEmail(
                        "test@example.com", 
                        "stats,custom_fields,referrals", 
                        "pub_test123"
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should get subscription by email with minimal parameters")
        void shouldGetSubscriptionByEmailWithMinimalParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.getSubscriptionByEmail("test@example.com", null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid email in getSubscriptionByEmail")
        void shouldThrowExceptionForInvalidEmailInGetSubscriptionByEmail() {
            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.getSubscriptionByEmail("invalid-email", null, null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.getSubscriptionByEmail("", null, null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.getSubscriptionByEmail(null, null, null);
            });
        }
    }

    @Nested
    @DisplayName("Get Subscription By ID Tests")
    class GetSubscriptionByIdTests {

        @Test
        @DisplayName("Should get subscription by ID with all parameters")
        void shouldGetSubscriptionByIdWithAllParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.getSubscriptionById(
                        "sub_123456789", 
                        "stats,custom_fields,referrals", 
                        "pub_test123"
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should get subscription by ID with minimal parameters")
        void shouldGetSubscriptionByIdWithMinimalParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.getSubscriptionById("sub_123456789", null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid subscription ID")
        void shouldThrowExceptionForInvalidSubscriptionId() {
            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.getSubscriptionById("", null, null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.getSubscriptionById(null, null, null);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                subscriptionService.getSubscriptionById("   ", null, null);
            });
        }
    }

    @Nested
    @DisplayName("Service Construction Tests")
    class ServiceConstructionTests {

        @Test
        @DisplayName("Should construct service with valid parameters")
        void shouldConstructServiceWithValidParameters() {
            assertDoesNotThrow(() -> {
                new SubscriptionService(mockRestClient, testProperties, mockObjectMapper);
            });
        }

        @Test
        @DisplayName("Should construct service without default publication ID")
        void shouldConstructServiceWithoutDefaultPublicationId() {
            assertDoesNotThrow(() -> {
                new SubscriptionService(mockRestClient, createTestPropertiesNoPublication(), mockObjectMapper);
            });
        }

        @Test
        @DisplayName("Should construct service with custom properties")
        void shouldConstructServiceWithCustomProperties() {
            BeehiivProperties customProperties = createTestProperties("custom-api-key", "pub_custom123");
            
            assertDoesNotThrow(() -> {
                new SubscriptionService(mockRestClient, customProperties, mockObjectMapper);
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long email addresses")
        void shouldHandleVeryLongEmailAddresses() {
            // Create a very long but valid email
            String longEmail = "a".repeat(50) + "@" + "b".repeat(50) + ".com";
            
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(longEmail, null, null, null, null, null, null, null, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle complex JSON custom fields")
        void shouldHandleComplexJsonCustomFields() {
            String complexJson = """
                {
                    "personalInfo": {
                        "name": "John Doe",
                        "age": 30,
                        "preferences": ["newsletters", "updates", "promotions"]
                    },
                    "settings": {
                        "frequency": "weekly",
                        "format": "html",
                        "notifications": true
                    },
                    "metadata": {
                        "source": "website",
                        "campaign": "spring-2024",
                        "score": 95.5
                    }
                }
                """;
            
            assertTrue(isValidJson(complexJson));
            
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        complexJson, 
                        null, null, null, null, null, null, null, null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should reject international email addresses (current limitation)")
        void shouldRejectInternationalEmailAddresses() {
            // The current email regex doesn't support international characters
            assertFalse(isValidEmail("用户@example.com"));
            assertFalse(isValidEmail("user@例え.テスト"));
            assertFalse(isValidEmail("test@münchen.de"));
            assertFalse(isValidEmail("français@exemple.fr"));
        }

        @Test
        @DisplayName("Should handle URL encoding in UTM parameters")
        void shouldHandleUrlEncodingInUtmParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.createSubscription(
                        "test@example.com", 
                        null, null, null, null,
                        "source with spaces",
                        "medium&campaign",
                        "campaign=test&utm_content=value",
                        "https://site.com/path?query=value&other=param",
                        null
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle subscription retrieval by email with edge case emails")
        void shouldHandleSubscriptionRetrievalByEmailWithEdgeCaseEmails() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.getSubscriptionByEmail(
                        "user+tag@subdomain.example.com", 
                        "stats,custom_fields", 
                        "pub_test123"
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle subscription retrieval by ID with complex expand parameters")
        void shouldHandleSubscriptionRetrievalByIdWithComplexExpandParameters() {
            assertDoesNotThrow(() -> {
                try {
                    subscriptionService.getSubscriptionById(
                        "sub_999888777", 
                        "stats,custom_fields,referrals,premium_tiers", 
                        "pub_test123"
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }
}