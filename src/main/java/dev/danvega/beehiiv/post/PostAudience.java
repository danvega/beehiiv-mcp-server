package dev.danvega.beehiiv.post;

public enum PostAudience {
    FREE("free"),
    PREMIUM("premium"),
    ALL("all");
    
    private final String value;
    
    PostAudience(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PostAudience fromString(String value) {
        if (value == null) {
            return ALL;
        }
        
        for (PostAudience audience : PostAudience.values()) {
            if (audience.value.equalsIgnoreCase(value)) {
                return audience;
            }
        }
        
        throw new IllegalArgumentException("Invalid post audience: " + value + 
            ". Valid values are: free, premium, all");
    }
}