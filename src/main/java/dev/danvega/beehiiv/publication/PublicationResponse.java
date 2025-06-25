package dev.danvega.beehiiv.publication;

public record PublicationResponse(Publication data) {
    // Validation constructor
    public PublicationResponse {
        // No validation needed for now, but this is where we would add it if needed
    }
}
