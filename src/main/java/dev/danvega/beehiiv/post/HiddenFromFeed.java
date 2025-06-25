package dev.danvega.beehiiv.post;

public enum HiddenFromFeed {
    TRUE("true"),
    FALSE("false"),
    ALL("all");
    
    private final String value;
    
    HiddenFromFeed(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static HiddenFromFeed fromString(String value) {
        if (value == null) {
            return ALL;
        }
        
        for (HiddenFromFeed hidden : HiddenFromFeed.values()) {
            if (hidden.value.equalsIgnoreCase(value)) {
                return hidden;
            }
        }
        
        throw new IllegalArgumentException("Invalid hidden_from_feed value: " + value + 
            ". Valid values are: true, false, all");
    }
}