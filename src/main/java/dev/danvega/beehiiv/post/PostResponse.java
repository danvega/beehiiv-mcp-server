package dev.danvega.beehiiv.post;

public record PostResponse(Post data) {
    // Validation constructor
    public PostResponse {
        // No validation needed for now, but this is where we would add it if needed
    }
}
