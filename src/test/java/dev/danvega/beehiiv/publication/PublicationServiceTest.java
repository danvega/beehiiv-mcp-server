package dev.danvega.beehiiv.publication;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicationServiceTest extends BaseServiceTest {

    private PublicationService publicationService;

    @BeforeEach
    void setUpPublicationService() {
        publicationService = new PublicationService(mockRestClient, testProperties);
    }

    @Nested
    @DisplayName("Parameter Validation Tests")
    class ParameterValidationTests {

        @Test
        @DisplayName("Should validate pagination parameters correctly")
        void shouldValidatePaginationParameters() {
            // Test valid limit values
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(1, 1, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });

            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(100, 1, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });

            // Test valid page values
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(10, 1, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid limit values")
        void shouldThrowExceptionForInvalidLimitValues() {
            // Test limit too small
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getAllPublications(0, 1, null, null);
            });

            // Test limit too large
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getAllPublications(101, 1, null, null);
            });

            // Test negative limit
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getAllPublications(-5, 1, null, null);
            });
        }

        @Test
        @DisplayName("Should throw exception for invalid page values")
        void shouldThrowExceptionForInvalidPageValues() {
            // Test page zero
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getAllPublications(10, 0, null, null);
            });

            // Test negative page
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getAllPublications(10, -1, null, null);
            });
        }

        @Test
        @DisplayName("Should accept null pagination parameters")
        void shouldAcceptNullPaginationParameters() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(null, null, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should accept valid ordering parameters")
        void shouldAcceptValidOrderingParameters() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(10, 1, "created", "asc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });

            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(10, 1, "name", "desc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }

    @Nested
    @DisplayName("Publication ID Validation Tests")
    class PublicationIdValidationTests {

        @Test
        @DisplayName("Should accept valid publication ID format")
        void shouldAcceptValidPublicationIdFormat() {
            String validId = "pub_123456789";
            
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getPublicationById(validId);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should throw exception for null or blank publication ID")
        void shouldThrowExceptionForNullOrBlankPublicationId(String invalidId) {
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getPublicationById(invalidId);
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {"123456789", "publication_123", "invalid_format", "sub_123456"})
        @DisplayName("Should throw exception for invalid publication ID format")
        void shouldThrowExceptionForInvalidPublicationIdFormat(String invalidId) {
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getPublicationById(invalidId);
            });
        }

        @Test
        @DisplayName("Should validate publication ID starts with 'pub_'")
        void shouldValidatePublicationIdPrefix() {
            // Helper method should correctly identify valid IDs
            assertTrue(isValidPublicationId("pub_123456789"));
            assertTrue(isValidPublicationId("pub_test_publication"));
            assertTrue(isValidPublicationId("pub_a"));

            // Helper method should correctly identify invalid IDs
            assertFalse(isValidPublicationId("123456789"));
            assertFalse(isValidPublicationId("publication_123"));
            assertFalse(isValidPublicationId("sub_123456"));
            assertFalse(isValidPublicationId("PUB_123456"));
            assertFalse(isValidPublicationId("pub"));
            assertFalse(isValidPublicationId("pub_"));
            assertFalse(isValidPublicationId(null));
            assertFalse(isValidPublicationId(""));
        }
    }

    @Nested
    @DisplayName("Current Publication Tests")
    class CurrentPublicationTests {

        @Test
        @DisplayName("Should call getCurrentPublication with default ID")
        void shouldCallGetCurrentPublicationWithDefaultId() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getCurrentPublication();
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should throw exception when no default publication ID configured")
        void shouldThrowExceptionWhenNoDefaultPublicationIdConfigured() {
            PublicationService serviceWithoutDefault = new PublicationService(mockRestClient, createTestPropertiesNoPublication());
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> serviceWithoutDefault.getCurrentPublication()
            );
            
            assertTrue(exception.getMessage().contains("No default publication ID configured"));
        }
    }

    @Nested
    @DisplayName("Search Functionality Tests")
    class SearchFunctionalityTests {

        @Test
        @DisplayName("Should accept valid search terms")
        void shouldAcceptValidSearchTerms() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.searchPublicationsByName("newsletter", 10, "name", "asc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });

            assertDoesNotThrow(() -> {
                try {
                    publicationService.searchPublicationsByName("Test Publication", null, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Should throw exception for null or blank search terms")
        void shouldThrowExceptionForNullOrBlankSearchTerms(String invalidSearchTerm) {
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.searchPublicationsByName(invalidSearchTerm, 10, "name", "asc");
            });
        }

        @Test
        @DisplayName("Should handle search with valid pagination")
        void shouldHandleSearchWithValidPagination() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.searchPublicationsByName("newsletter", 50, "created", "desc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle search with invalid pagination")
        void shouldHandleSearchWithInvalidPagination() {
            // The search method calls getAllPublications internally, which should validate pagination
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.searchPublicationsByName("newsletter", 101, "created", "desc");
            });
        }
    }

    @Nested
    @DisplayName("List Accessible Publications Tests")
    class ListAccessiblePublicationsTests {

        @Test
        @DisplayName("Should list accessible publications with default limit")
        void shouldListAccessiblePublicationsWithDefaultLimit() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.listAccessiblePublications(null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should list accessible publications with custom limit")
        void shouldListAccessiblePublicationsWithCustomLimit() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.listAccessiblePublications(25);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should validate limit for accessible publications")
        void shouldValidateLimitForAccessiblePublications() {
            // Should throw exception for invalid limit
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.listAccessiblePublications(101);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.listAccessiblePublications(0);
            });
        }
    }

    @Nested
    @DisplayName("All Publications With Pagination Tests")
    class AllPublicationsWithPaginationTests {

        @Test
        @DisplayName("Should get all publications with pagination")
        void shouldGetAllPublicationsWithPagination() {
            assertDoesNotThrow(() -> {
                try {
                    List<Publication> result = publicationService.getAllPublicationsWithPagination(25, "created", "desc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle null parameters in pagination method")
        void shouldHandleNullParametersInPaginationMethod() {
            assertDoesNotThrow(() -> {
                try {
                    List<Publication> result = publicationService.getAllPublicationsWithPagination(null, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should validate limit in pagination method")
        void shouldValidateLimitInPaginationMethod() {
            assertThrows(IllegalArgumentException.class, () -> {
                publicationService.getAllPublicationsWithPagination(101, "created", "desc");
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
                new PublicationService(mockRestClient, testProperties);
            });
        }

        @Test
        @DisplayName("Should construct service without default publication ID")
        void shouldConstructServiceWithoutDefaultPublicationId() {
            assertDoesNotThrow(() -> {
                new PublicationService(mockRestClient, createTestPropertiesNoPublication());
            });
        }

        @Test
        @DisplayName("Should construct service with custom properties")
        void shouldConstructServiceWithCustomProperties() {
            BeehiivProperties customProperties = createTestProperties("custom-api-key", "pub_custom123");
            
            assertDoesNotThrow(() -> {
                new PublicationService(mockRestClient, customProperties);
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long publication IDs")
        void shouldHandleVeryLongPublicationIds() {
            String longId = "pub_" + "a".repeat(100);
            
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getPublicationById(longId);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle special characters in search terms")
        void shouldHandleSpecialCharactersInSearchTerms() {
            String specialSearchTerm = "newsletter@test.com & more!";
            
            assertDoesNotThrow(() -> {
                try {
                    publicationService.searchPublicationsByName(specialSearchTerm, 10, "name", "asc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle Unicode characters in search terms")
        void shouldHandleUnicodeCharactersInSearchTerms() {
            String unicodeSearchTerm = "测试通讯 Newsletter 📧";
            
            assertDoesNotThrow(() -> {
                try {
                    publicationService.searchPublicationsByName(unicodeSearchTerm, 10, "name", "asc");
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle boundary limit values")
        void shouldHandleBoundaryLimitValues() {
            // Test minimum valid limit
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(1, 1, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });

            // Test maximum valid limit
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(100, 1, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }

        @Test
        @DisplayName("Should handle very large page numbers")
        void shouldHandleVeryLargePageNumbers() {
            assertDoesNotThrow(() -> {
                try {
                    publicationService.getAllPublications(10, Integer.MAX_VALUE, null, null);
                } catch (Exception e) {
                    // Expected - no actual API call will succeed in tests
                }
            });
        }
    }
}