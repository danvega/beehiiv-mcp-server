package dev.danvega.beehiiv.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    public RestClient beehiivRestClient(BeehiivProperties beehiivProperties) {
        String apiKey = beehiivProperties.api();
        
        logger.info("Configuring Beehiiv RestClient with API key: {}", 
                maskApiKey(apiKey));
        
        return RestClient.builder()
                .baseUrl(ApiConstants.BEEHIIV_API_BASE_URL)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "***masked***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
