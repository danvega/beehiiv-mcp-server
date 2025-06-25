package dev.danvega.beehiiv.post;

public enum PostPlatform {
    WEB("web"),
    EMAIL("email"),
    BOTH("both"),
    ALL("all");
    
    private final String value;
    
    PostPlatform(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PostPlatform fromString(String value) {
        if (value == null) {
            return ALL;
        }
        
        for (PostPlatform platform : PostPlatform.values()) {
            if (platform.value.equalsIgnoreCase(value)) {
                return platform;
            }
        }
        
        throw new IllegalArgumentException("Invalid post platform: " + value + 
            ". Valid values are: web, email, both, all");
    }
}