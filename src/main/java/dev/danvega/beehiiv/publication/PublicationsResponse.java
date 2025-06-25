package dev.danvega.beehiiv.publication;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PublicationsResponse(
    List<Publication> data,
    Integer limit,
    Integer page,
    
    @JsonProperty("total_results")
    Integer totalResults,
    
    @JsonProperty("total_pages")
    Integer totalPages
) {
    // Validation constructor
    public PublicationsResponse {
        // No validation needed for now, but this is where we would add it if needed
    }
}
