package dev.danvega.beehiiv.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "beehiiv")
public record BeehiivProperties(String api, String defaultPublicationId) {
    
    @ConstructorBinding
    public BeehiivProperties {
        if (api == null || api.isBlank()) {
            throw new IllegalArgumentException("Beehiiv API key must not be null or blank");
        }
        
        // Default publication ID is optional - can be null for multi-publication usage
        if (defaultPublicationId != null && !defaultPublicationId.isBlank()) {
            if (!defaultPublicationId.startsWith("pub_")) {
                throw new IllegalArgumentException("Beehiiv default publication ID must start with 'pub_'");
            }
        }
    }
    
    /**
     * @return true if a default publication ID is configured
     */
    public boolean hasDefaultPublicationId() {
        return defaultPublicationId != null && !defaultPublicationId.isBlank();
    }
}
