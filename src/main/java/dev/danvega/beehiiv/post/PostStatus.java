package dev.danvega.beehiiv.post;

public enum PostStatus {
    DRAFT("draft"),
    CONFIRMED("confirmed"), 
    ARCHIVED("archived"),
    ALL("all");
    
    private final String value;
    
    PostStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PostStatus fromString(String value) {
        if (value == null) {
            return ALL;
        }
        
        for (PostStatus status : PostStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Invalid post status: " + value + 
            ". Valid values are: draft, confirmed, archived, all");
    }
}