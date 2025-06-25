package dev.danvega.beehiiv;

import dev.danvega.beehiiv.core.BeehiivProperties;
import dev.danvega.beehiiv.post.PostService;
import dev.danvega.beehiiv.publication.PublicationService;
import dev.danvega.beehiiv.subscription.SubscriptionService;
import dev.danvega.beehiiv.subscription.CustomFieldService;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(BeehiivProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public List<ToolCallback> beehiivToolCallbacks(PostService postService, PublicationService publicationService, 
                                                  SubscriptionService subscriptionService, CustomFieldService customFieldService) {
        var postCallbacks = ToolCallbacks.from(postService);
        var publicationCallbacks = ToolCallbacks.from(publicationService);
        var subscriptionCallbacks = ToolCallbacks.from(subscriptionService);
        var customFieldCallbacks = ToolCallbacks.from(customFieldService);
        
        List<ToolCallback> allCallbacks = new ArrayList<>();
        allCallbacks.addAll(Arrays.asList(postCallbacks));
        allCallbacks.addAll(Arrays.asList(publicationCallbacks));
        allCallbacks.addAll(Arrays.asList(subscriptionCallbacks));
        allCallbacks.addAll(Arrays.asList(customFieldCallbacks));
        
        return allCallbacks;
    }
}
