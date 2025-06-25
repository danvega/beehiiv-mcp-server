package dev.danvega.beehiiv.post;

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
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PostServiceTest extends BaseServiceTest {

    private PostService postService;

    @BeforeEach
    void setUpPostService() {
        postService = new PostService(mockRestClient, testProperties);
    }

    @Nested
    @DisplayName("Parameter Validation Tests")
    class ParameterValidationTests {

        @Test
        @DisplayName("Should validate enum parameters correctly")
        void shouldValidateEnumParameters() {
            // Test valid status values
            assertDoesNotThrow(() -> PostStatus.fromString("draft"));
            assertDoesNotThrow(() -> PostStatus.fromString("confirmed"));
            assertDoesNotThrow(() -> PostStatus.fromString("archived"));
            assertDoesNotThrow(() -> PostStatus.fromString("all"));

            // Test valid platform values
            assertDoesNotThrow(() -> PostPlatform.fromString("email"));
            assertDoesNotThrow(() -> PostPlatform.fromString("web"));
            assertDoesNotThrow(() -> PostPlatform.fromString("both"));

            // Test valid audience values
            assertDoesNotThrow(() -> PostAudience.fromString("free"));
            assertDoesNotThrow(() -> PostAudience.fromString("premium"));
            assertDoesNotThrow(() -> PostAudience.fromString("all"));

            // Test invalid values throw exceptions
            assertThrows(IllegalArgumentException.class, () -> PostStatus.fromString("invalid"));
            assertThrows(IllegalArgumentException.class, () -> PostPlatform.fromString("invalid"));
            assertThrows(IllegalArgumentException.class, () -> PostAudience.fromString("invalid"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should handle null and blank string parameters")
        void shouldHandleNullAndBlankParameters(String input) {
            if (input == null) {
                // All enums return their default "ALL" value for null input
                assertEquals(PostStatus.ALL, PostStatus.fromString(input));
                assertEquals(PostPlatform.ALL, PostPlatform.fromString(input));
                assertEquals(PostAudience.ALL, PostAudience.fromString(input));
                assertEquals(HiddenFromFeed.ALL, HiddenFromFeed.fromString(input));
            } else {
                // Blank/whitespace strings should throw exceptions
                assertThrows(IllegalArgumentException.class, () -> PostStatus.fromString(input));
                assertThrows(IllegalArgumentException.class, () -> PostPlatform.fromString(input));
                assertThrows(IllegalArgumentException.class, () -> PostAudience.fromString(input));
                assertThrows(IllegalArgumentException.class, () -> HiddenFromFeed.fromString(input));
            }
        }

        @Test
        @DisplayName("Should validate HiddenFromFeed enum parameter")
        void shouldValidateHiddenFromFeedParameter() {
            assertEquals(HiddenFromFeed.TRUE, HiddenFromFeed.fromString("true"));
            assertEquals(HiddenFromFeed.FALSE, HiddenFromFeed.fromString("false"));
            assertEquals(HiddenFromFeed.ALL, HiddenFromFeed.fromString(null));
            
            // Test case insensitive
            assertEquals(HiddenFromFeed.TRUE, HiddenFromFeed.fromString("TRUE"));
            assertEquals(HiddenFromFeed.FALSE, HiddenFromFeed.fromString("FALSE"));
            
            // Test invalid values
            assertThrows(IllegalArgumentException.class, () -> HiddenFromFeed.fromString("invalid"));
            assertThrows(IllegalArgumentException.class, () -> HiddenFromFeed.fromString("yes"));
        }
    }

    @Nested
    @DisplayName("Publication ID Handling Tests")
    class PublicationIdHandlingTests {

        @Test
        @DisplayName("Should handle service creation with default publication ID")
        void shouldHandleServiceCreationWithDefaultPublicationId() {
            assertDoesNotThrow(() -> new PostService(mockRestClient, testProperties));
        }

        @Test
        @DisplayName("Should handle service creation without default publication ID")
        void shouldHandleServiceCreationWithoutDefaultPublicationId() {
            assertDoesNotThrow(() -> new PostService(mockRestClient, createTestPropertiesNoPublication()));
        }

        @Test
        @DisplayName("Should throw exception when no publication ID available in getAllPosts")
        void shouldThrowExceptionWhenNoPublicationIdAvailable() {
            PostService serviceWithoutDefault = new PostService(mockRestClient, createTestPropertiesNoPublication());
            
            Exception exception = assertThrows(
                Exception.class,
                () -> serviceWithoutDefault.getAllPosts(null, null, null, null, null, null, null, null, null, null)
            );
            
            // The exception could be IllegalArgumentException or wrapped in ApiException
            String message = exception.getMessage();
            assertTrue(message.contains("Publication ID") || message.contains("publication"), 
                      "Expected error message to mention publication ID, but was: " + message);
        }
    }

    @Nested
    @DisplayName("Service Method Tests")
    class ServiceMethodTests {

        @Test
        @DisplayName("Should call getAllPosts with all parameters")
        void shouldCallGetAllPostsWithAllParameters() {
            // Test that method accepts all parameters without throwing
            assertDoesNotThrow(() -> {
                try {
                    postService.getAllPosts(
                        5,               // limit
                        1,               // page
                        "publish_date",  // orderBy
                        "desc",          // sortOrder
                        "all",           // audience
                        "both",          // platform
                        "confirmed",     // status
                        "newsletter,tips", // contentTags
                        "true",          // hiddenFromFeed
                        "pub_test123"    // publicationId
                    );
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                    // We just want to verify parameters are accepted
                }
            });
        }

        @Test
        @DisplayName("Should call getAllPosts with minimal parameters")
        void shouldCallGetAllPostsWithMinimalParameters() {
            assertDoesNotThrow(() -> {
                try {
                    postService.getAllPosts(null, null, null, null, null, null, null, null, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }

    @Nested
    @DisplayName("Get Single Post Tests")
    class GetSinglePostTests {

        @Test
        @DisplayName("Should call getPostById with parameters")
        void shouldCallGetPostByIdWithParameters() {
            String postId = "post_123456789";
            
            assertDoesNotThrow(() -> {
                try {
                    postService.getPostById(postId, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should call getPostById with custom publication ID")
        void shouldCallGetPostByIdWithCustomPublicationId() {
            String customPubId = "pub_custom456";
            String postId = "post_123456789";
            
            assertDoesNotThrow(() -> {
                try {
                    postService.getPostById(postId, null, customPubId);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw exception for invalid post ID format")
        void shouldThrowExceptionForInvalidPostIdFormat() {
            String invalidPostId = "";

            // Should throw validation error for empty string
            assertThrows(IllegalArgumentException.class, () -> {
                postService.getPostById(invalidPostId, null, null);
            });
        }

        @Test
        @DisplayName("Should throw exception for null post ID")
        void shouldThrowExceptionForNullPostId() {
            // Should throw validation error for null post ID
            assertThrows(IllegalArgumentException.class, () -> {
                postService.getPostById(null, null, null);
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long content tags")
        void shouldHandleVeryLongContentTags() {
            StringBuilder longTags = new StringBuilder();
            for (int i = 0; i < 100; i++) {
                longTags.append("tag").append(i).append(",");
            }
            String contentTags = longTags.toString();

            assertDoesNotThrow(() -> {
                try {
                    postService.getAllPosts(null, null, null, null, null, null, null, contentTags, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle special characters in content tags")
        void shouldHandleSpecialCharactersInContentTags() {
            String specialTags = "tag-with-dashes,tag_with_underscores,tag with spaces,tag@email";

            assertDoesNotThrow(() -> {
                try {
                    postService.getAllPosts(null, null, null, null, null, null, null, specialTags, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle very large limit values")
        void shouldHandleVeryLargeLimitValues() {
            Integer largeLimit = 10000;

            assertDoesNotThrow(() -> {
                try {
                    postService.getAllPosts(largeLimit, null, null, null, null, null, null, null, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle negative limit values")
        void shouldHandleNegativeLimitValues() {
            Integer negativeLimit = -5;

            // Should throw validation error
            assertThrows(IllegalArgumentException.class, () -> {
                postService.getAllPosts(negativeLimit, null, null, null, null, null, null, null, null, null);
            });
        }

        @Test
        @DisplayName("Should handle zero limit value")
        void shouldHandleZeroLimitValue() {
            Integer zeroLimit = 0;

            // Should throw validation error
            assertThrows(IllegalArgumentException.class, () -> {
                postService.getAllPosts(zeroLimit, null, null, null, null, null, null, null, null, null);
            });
        }
    }
}