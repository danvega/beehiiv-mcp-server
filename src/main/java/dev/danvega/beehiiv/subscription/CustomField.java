package dev.danvega.beehiiv.subscription;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

public record CustomField(
    String id,
    
    String name,
    
    String type,
    
    @JsonProperty("is_required")
    Boolean isRequired,
    
    @JsonProperty("created_at")
    LocalDateTime createdAt,
    
    @JsonProperty("updated_at")
    LocalDateTime updatedAt,
    
    @JsonProperty("default_value")
    Object defaultValue,
    
    @JsonProperty("options")
    List<String> options,
    
    @JsonProperty("description")
    String description
) {
    /**
     * Validates a value against this custom field's type and constraints
     * @param value The value to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidValue(Object value) {
        if (value == null) {
            return !Boolean.TRUE.equals(isRequired);
        }
        
        FieldType fieldType = FieldType.fromString(type);
        return fieldType.isValidValue(value, options);
    }
    
    /**
     * Converts a string value to the appropriate type for this field
     * @param stringValue The string representation of the value
     * @return The converted value
     * @throws IllegalArgumentException if conversion fails
     */
    public Object convertValue(String stringValue) {
        if (stringValue == null || stringValue.isBlank()) {
            return null;
        }
        
        FieldType fieldType = FieldType.fromString(type);
        return fieldType.convertValue(stringValue, options);
    }
    
    /**
     * Gets the field type as an enum
     * @return FieldType enum value
     */
    public FieldType getFieldType() {
        return FieldType.fromString(type);
    }
    
    public enum FieldType {
        TEXT("text", "string"),
        NUMBER("number"),
        BOOLEAN("boolean"),
        DATE("date"),
        DATETIME("datetime"),
        LIST("list");
        
        private final String[] values;
        
        FieldType(String... values) {
            this.values = values;
        }
        
        public String getValue() {
            return values[0];
        }
        
        public static FieldType fromString(String value) {
            if (value == null) {
                return TEXT;
            }
            
            for (FieldType type : FieldType.values()) {
                for (String typeValue : type.values) {
                    if (typeValue.equalsIgnoreCase(value)) {
                        return type;
                    }
                }
            }
            
            throw new IllegalArgumentException("Invalid custom field type: " + value + 
                ". Valid types are: text/string, number, boolean, date, datetime, list");
        }
        
        /**
         * Validates if a value is compatible with this field type
         */
        public boolean isValidValue(Object value, List<String> options) {
            if (value == null) {
                return true;
            }
            
            return switch (this) {
                case TEXT -> value instanceof String;
                case NUMBER -> value instanceof Number || isNumericString(value.toString());
                case BOOLEAN -> value instanceof Boolean || isBooleanString(value.toString());
                case DATE, DATETIME -> value instanceof String; // Accept ISO date strings
                case LIST -> value instanceof String && (options == null || options.contains(value.toString()));
            };
        }
        
        /**
         * Converts a string value to the appropriate Java type
         */
        public Object convertValue(String stringValue, List<String> options) {
            if (stringValue == null || stringValue.isBlank()) {
                return null;
            }
            
            return switch (this) {
                case TEXT -> stringValue;
                case NUMBER -> {
                    try {
                        if (stringValue.contains(".")) {
                            yield Double.parseDouble(stringValue);
                        } else {
                            yield Integer.parseInt(stringValue);
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid number format: " + stringValue);
                    }
                }
                case BOOLEAN -> {
                    String lower = stringValue.toLowerCase();
                    if ("true".equals(lower) || "1".equals(lower) || "yes".equals(lower)) {
                        yield true;
                    } else if ("false".equals(lower) || "0".equals(lower) || "no".equals(lower)) {
                        yield false;
                    } else {
                        throw new IllegalArgumentException("Invalid boolean format: " + stringValue);
                    }
                }
                case DATE, DATETIME -> stringValue; // Return as string, let API handle parsing
                case LIST -> {
                    if (options != null && !options.contains(stringValue)) {
                        throw new IllegalArgumentException("Invalid list option: " + stringValue + 
                            ". Valid options: " + String.join(", ", options));
                    }
                    yield stringValue;
                }
            };
        }
        
        private static boolean isNumericString(String str) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        private static boolean isBooleanString(String str) {
            String lower = str.toLowerCase();
            return "true".equals(lower) || "false".equals(lower) || "1".equals(lower) || 
                   "0".equals(lower) || "yes".equals(lower) || "no".equals(lower);
        }
    }
}