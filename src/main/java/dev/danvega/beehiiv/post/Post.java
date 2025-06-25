package dev.danvega.beehiiv.post;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record Post(
    String id,
    String title,
    String subtitle,
    String slug,
    
    String created,
    
    @JsonProperty("created_at")
    LocalDateTime createdAt,
    
    @JsonProperty("publish_date")
    Long publishDate,
    
    @JsonProperty("displayed_date")
    Long displayedDate,
    
    @JsonProperty("published_at")
    LocalDateTime publishedAt,
    
    @JsonProperty("updated_at")
    LocalDateTime updatedAt,
    
    String status,
    
    @JsonProperty("free_unlock")
    boolean freeUnlock,
    
    @JsonProperty("split_tested")
    boolean splitTested,
    
    List<String> authors,
    
    @JsonProperty("subject_line")
    String subjectLine,
    
    @JsonProperty("preview_text")
    String previewText,
    
    @JsonProperty("thumbnail_url")
    String thumbnailUrl,
    
    @JsonProperty("web_url")
    String webUrl,
    
    String audience,
    
    String platform,
    
    @JsonProperty("audience_types")
    List<String> audienceTypes,
    
    @JsonProperty("content_tags")
    List<String> contentTags,
    
    @JsonProperty("hidden_from_feed")
    boolean hiddenFromFeed,
    
    @JsonProperty("meta_default_description")
    String metaDefaultDescription,
    
    @JsonProperty("meta_default_title")
    String metaDefaultTitle,
    
    Map<String, Map<String, String>> content,
    
    Map<String, Object> stats,
    
    List<Map<String, Object>> clicks,
    
    @JsonProperty("content_html")
    String contentHtml,
    
    @JsonProperty("content_markdown")
    String contentMarkdown,
    
    @JsonProperty("content_summary")
    String contentSummary,
    
    @JsonProperty("free_web_content")
    String freeWebContent,
    
    @JsonProperty("premium_web_content")
    String premiumWebContent,
    
    @JsonProperty("premium_email_content")
    String premiumEmailContent,
    
    @JsonProperty("free_rss_content")
    String freeRssContent,
    
    @JsonProperty("scheduled_at")
    LocalDateTime scheduledAt,
    
    @JsonProperty("email_sent_at")
    LocalDateTime emailSentAt,
    
    @JsonProperty("web_published_at")
    LocalDateTime webPublishedAt,
    
    @JsonProperty("email_delivered_count")
    Integer emailDeliveredCount,
    
    @JsonProperty("email_open_count")
    Integer emailOpenCount,
    
    @JsonProperty("email_click_count")
    Integer emailClickCount,
    
    @JsonProperty("web_view_count")
    Integer webViewCount,
    
    @JsonProperty("total_click_count")
    Integer totalClickCount,
    
    @JsonProperty("post_template_id")
    String postTemplateId,
    
    @JsonProperty("publication_id")
    String publicationId
) {
    // Validation constructor
    public Post {
        // No validation needed for now, but this is where we would add it if needed
    }
}
