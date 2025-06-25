package dev.danvega.beehiiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.danvega.beehiiv.core.BeehiivProperties;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseServiceTest {

    protected RestClient mockRestClient;
    protected BeehiivProperties testProperties;
    protected ObjectMapper mockObjectMapper;
    
    @BeforeEach
    void setUp() {
        mockRestClient = mock(RestClient.class);
        testProperties = new BeehiivProperties("test-api-key-12345", "pub_test123");
        mockObjectMapper = new ObjectMapper(); // Use real ObjectMapper for JSON testing
    }
    
    /**
     * Helper method to create test properties with custom values
     */
    protected BeehiivProperties createTestProperties(String apiKey, String publicationId) {
        return new BeehiivProperties(apiKey, publicationId);
    }
    
    /**
     * Helper method to create test properties with no default publication ID
     */
    protected BeehiivProperties createTestPropertiesNoPublication() {
        return new BeehiivProperties("test-api-key-12345", null);
    }
    
    /**
     * Helper method to validate email format
     */
    protected boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
    }
    
    /**
     * Helper method to validate JSON string
     */
    protected boolean isValidJson(String json) {
        if (json == null || json.isBlank()) {
            return false;
        }
        try {
            mockObjectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Helper method to validate publication ID format
     */
    protected boolean isValidPublicationId(String publicationId) {
        return publicationId != null && !publicationId.isBlank() && 
               publicationId.startsWith("pub_") && publicationId.length() > 4;
    }
}