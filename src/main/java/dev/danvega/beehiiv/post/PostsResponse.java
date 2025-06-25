package dev.danvega.beehiiv.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PostsResponse(
    List<Post> data,
    Pagination pagination
) {
    // Validation constructor
    public PostsResponse {
        // No validation needed for now, but this is where we would add it if needed
    }
    
    public record Pagination(
        int count,
        @JsonProperty("next_page")
        String nextPage,
        @JsonProperty("prev_page")
        String prevPage
    ) {
        // Validation constructor
        public Pagination {
            // No validation needed for now, but this is where we would add it if needed
        }
    }
}
